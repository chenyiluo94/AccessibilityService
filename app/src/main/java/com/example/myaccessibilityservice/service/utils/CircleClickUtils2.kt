package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag

class CircleClickUtils2 {
    val TAG = "CircleClickUtils2"
    lateinit var accessibilityService: AccessibilityService

    companion object {
        //开启自动点赞
        var OPEN_CIRCLE_ZAN = true

        //是否是第一次进来
        var IS_FRIST = true
        var IS_SCROLLED = false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(accessibilityService: AccessibilityService, event: AccessibilityEvent) {
        this.accessibilityService = accessibilityService
        cicleClick(event)
    }

    //朋友圈点赞
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun cicleClick(event: AccessibilityEvent) {
        if (OPEN_CIRCLE_ZAN) {
            if (IS_FRIST) {
                JumpContactsUtlis(accessibilityService, event, "发现")
                val eventType = event.eventType
                when (eventType) {
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                        val nodeInfo = accessibilityService.rootInActiveWindow
                        val list =
                            nodeInfo.findAccessibilityNodeInfosByText("朋友圈")
                        if (list.size > 0) {
                            for (nodeInfo in list) {
                                if (nodeInfo != null && nodeInfo.text != null && nodeInfo.text.equals(
                                        "朋友圈"
                                    ) && nodeInfo.className.equals(
                                        CommonTag.TextView
                                    )
                                ) {
                                    CommonToolUtils.performClick(nodeInfo)
                                    IS_FRIST = false
                                }
                            }
                        }
                    }
                }
            }
            realCircle(event)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun realCircle(event: AccessibilityEvent) {
        var className = event.className
        when (className) {
            CommonTag.CIRCLE_PAGE -> {
                circle(event)
            }
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                if (IS_SCROLLED) {
                    circle(event)
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun circle(event: AccessibilityEvent) {
        val rootNode = accessibilityService.rootInActiveWindow
        if (rootNode != null) {
            val listNodes = rootNode.findAccessibilityNodeInfosByViewId(CommonTag.CIRCLE_LISTVIEW)
            if (listNodes != null && listNodes.size > 0) {
                val listNode = listNodes[0]
                //赞的按钮
                val zanNodes = listNode.findAccessibilityNodeInfosByViewId(CommonTag.CIRCLE_ZAN_ITEM)
                for (zan in zanNodes) {
                    zan.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Thread.sleep(300)
                    //赞的节点
                    val zsNodes =
                        accessibilityService.rootInActiveWindow.findAccessibilityNodeInfosByViewId(CommonTag.CIRCLE_ZAN_POP)
                    Thread.sleep(300)
                    if (zsNodes != null && zsNodes.size > 0) {
                        if (zsNodes[0].findAccessibilityNodeInfosByText("赞").size > 0) {
                            zsNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }
                    Thread.sleep(300)
                }
                listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                IS_SCROLLED = true
            }
        } else {
            Log.i(TAG, "============为空")
        }
    }
}