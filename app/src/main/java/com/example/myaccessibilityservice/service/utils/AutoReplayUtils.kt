package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.utils.PhoneControllerUtils

class AutoReplayUtils {
    val TAG = "AutoReplayUtils"
    private var hasNotify = false

    var handler: Handler = object : Handler() {}
    var accessibilityService: AccessibilityService

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(accessibilityService: AccessibilityService, event: AccessibilityEvent) {
        this.accessibilityService = accessibilityService
        aoto(event)
    }

    /**
     * 自动回复
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun aoto(event: AccessibilityEvent) {
        val eventType = event.eventType // 事件类型

        when (eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                Log.i(TAG, "TYPE_NOTIFICATION_STATE_CHANGED")
                if (PhoneControllerUtils.isLockScreen(accessibilityService)) { // 锁屏
                    PhoneControllerUtils.wakeAndUnlockScreen(accessibilityService) // 唤醒点亮屏幕
                }
                this.hasNotify = true
                Log.i(TAG, "DEFAULT改变值" + this.hasNotify)
                openAppByNotification(event)
            }
            else -> {

            }
        }
    }

    /**
     * 查找UI控件并点击
     * @param widget 控件完整名称, 如android.widget.Button, android.widget.TextView
     * @param text 控件文本
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun findAndPerformAction(widget: String, text: String) {
        // 取得当前激活窗体的根节点
        if (accessibilityService.rootInActiveWindow == null) {
            return
        }

        // 通过文本找到当前的节点
        val nodes =
            accessibilityService.rootInActiveWindow.findAccessibilityNodeInfosByText(text)
        if (nodes != null) {
            for (node in nodes) {
                if (node.className == widget && node.isEnabled) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK) // 执行点击
                    break
                }
            }
        }
    }

    /**
     * 打开微信
     * @param event 事件
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun openAppByNotification(event: AccessibilityEvent) {
        if (event.parcelableData != null && event.parcelableData is Notification) {
            val notification =
                event.parcelableData as Notification
            try {
                val pendingIntent = notification.contentIntent
                pendingIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                e.printStackTrace()
            }

            if (this.hasNotify) { // 如果有通知
                try {
                    Thread.sleep(1000) // 停1秒, 否则在微信主界面没进入聊天界面就执行了fillInputBar
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (fillInputBar("我在忙，稍后回复哈~")) { // 找到输入框，即EditText
                    findAndPerformAction("android.widget.Button", "发送") // 点击发送
                    handler.postDelayed(
                        Runnable
                        // 返回主界面，这里延迟执行，为了有更好的交互
                        {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) // 返回
                        }, 1500
                    )
                }
                hasNotify = false
                Log.i(TAG, "手动改编成DEFAULT" + hasNotify)
            }

        }
    }

    /**
     * 填充输入框
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun fillInputBar(reply: String): Boolean {
        val rootNode = accessibilityService.rootInActiveWindow
        return rootNode?.let { findInputBar(it, reply) } ?: false
    }

    /**
     * 查找EditText控件
     * @param rootNode 根结点
     * @param reply 回复内容
     * @return 找到返回true, 否则返回false
     */
    private fun findInputBar(
        rootNode: AccessibilityNodeInfo,
        reply: String
    ): Boolean {
        val count = rootNode.childCount
        for (i in 0 until count) {
            val node = rootNode.getChild(i)
            if ("android.widget.EditText".equals(node.className)) {   // 找到输入框并输入文本
                setText(node, reply)
                return true
            }
            if (findInputBar(node, reply)) {    // 递归查找
                return true
            }
        }
        return false
    }

    /**
     * 设置文本
     */
    private fun setText(node: AccessibilityNodeInfo, reply: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val args = Bundle()
            args.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                reply
            )
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        } else {
            val data = ClipData.newPlainText("reply", reply)
            val clipboardManager: ClipboardManager =
                accessibilityService.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(data)
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS) // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE) // 执行粘贴
        }
    }
}