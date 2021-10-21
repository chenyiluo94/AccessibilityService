package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.PendingIntent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.utils.PhoneControllerUtils

class RedEnvelopesUtils {
    companion object {
        val TAG = "RedEnvelopesUtils"
        private var hasNotify = false

        //领取红包返回
        private val MSG_BACK_HOME = 0
        private val MSG_BACK_ONCE = 1
        var hasLuckyMoney = true

    }

    lateinit var accessibilityService: AccessibilityService


    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun handleMessage(msg: Message) {
            if (msg.what == MSG_BACK_HOME) {
                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                postDelayed({
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    hasLuckyMoney = false
                }, 1500)
            } else if (msg.what == MSG_BACK_ONCE) {
                postDelayed({
                    Log.i(TAG, "---------------------click back")
                    accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                    hasLuckyMoney = false
                    hasNotify = false
                }, 1500)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(accessibilityService: AccessibilityService, event: AccessibilityEvent) {
        this.accessibilityService = accessibilityService
        redEnvelopes(event)
    }

    /**
     * 抢红包
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun redEnvelopes(event: AccessibilityEvent) {
        val eventType = event.eventType // 事件类型

        when (eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                Log.i(TAG, "-------------------TYPE_NOTIFICATION_STATE_CHANGED")
                if (event.parcelableData != null && event.parcelableData is Notification) {
                    val notification = event.parcelableData as Notification
                    val content = notification.tickerText.toString()
                    Log.i("AccessibilityService", "content:--------------- $content")
                    if (content.contains("[微信红包]")) {
                        if (PhoneControllerUtils.isLockScreen(accessibilityService)) { // 锁屏
                            PhoneControllerUtils.wakeAndUnlockScreen(accessibilityService) // 唤醒点亮屏幕
                        }
                        hasNotify = true
                        openAppByNotification(event)
                        Log.i(TAG, "------------------11-DEFAULT" + hasNotify)
                    }
                }

            }
            else -> {
                Log.i(TAG, "-------------------DEFAULT" + hasNotify)
                if (hasNotify) {
                    val rootNode = accessibilityService.rootInActiveWindow
                    clickLuckyMoney(rootNode)
                    val className = event.className.toString()
                    if (className == CommonTag.LUCKY_MONEY_RECEIVE_UI) {
                        if (!openLuckyMoney()) {
                            backToHome()
                            hasNotify = false
                        }
                        hasLuckyMoney = true
                    } else if (className == CommonTag.LUCKY_MONEY_DETAIL_UI) {
                        backToHome()
                        hasNotify = false
                        hasLuckyMoney = true
                    } else {
                        if (!hasLuckyMoney) {
                            handler.sendEmptyMessage(MSG_BACK_ONCE)
                            hasLuckyMoney = true // 防止后退多次
                        }
                    }
                }
            }
        }
    }

    /**
     * 点击红包
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun clickLuckyMoney(rootNode: AccessibilityNodeInfo?) {

        if (rootNode != null) {
            val count = rootNode.childCount
            for (i in count - 1 downTo 0) {  // 倒序查找最新的红包
                val node = rootNode.getChild(i) ?: continue
                val text = node.text
                if (text != null && text.toString().contains("微信红包")) {
                    var parent = node.parent
                    while (parent != null) {
                        if (parent.isClickable) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            break
                        }
                        parent = parent.parent
                    }
                } else {
                    clickLuckyMoney(node)
                }

            }
        }
    }

    /**
     * 打开领取红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun openLuckyMoney(): Boolean {
        val rootNode = accessibilityService.rootInActiveWindow
        val count = rootNode.childCount
        for (i in count - 1 downTo 0) {
            val node = rootNode.getChild(i) ?: continue
            if (node.className.equals(CommonTag.BUTTON)) {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }

            }
        }
        return false
    }

    private fun backToHome() {
        if (handler.hasMessages(MSG_BACK_HOME)) {
            handler.removeMessages(MSG_BACK_HOME)
        }
        handler.sendEmptyMessage(MSG_BACK_HOME)
    }

    /**
     * 打开微信
     * @param event 事件
     */
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
        }
    }

}