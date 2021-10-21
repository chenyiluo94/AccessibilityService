package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag

class CircleClickUtils {
    val TAG = "CircleClickUtils"
    lateinit var accessibilityService: AccessibilityService

    companion object {
        //开启自动点赞
        var OPEN_CIRCLE_ZAN = true

        //是否是第一次进来
        var IS_FRIST = true
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
                                if (nodeInfo != null && nodeInfo.text.equals("朋友圈") && nodeInfo.className.equals(
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
        val eventType = event.eventType
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                var className = event.className
                when (className) {
                    CommonTag.CIRCLE_PAGE -> {
                        val nodeInfo = accessibilityService.rootInActiveWindow
                        val list =
                            nodeInfo.findAccessibilityNodeInfosByViewId(CommonTag.CIRCLE_ZAN_ITEM)
                        for (i in list.withIndex()) {
                            var nodeValue = i.value
                            if (i.index == 0) {
                                CommonToolUtils.performClick(nodeValue)
                            }
                        }
                    }
                }
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                val nodeInfo = accessibilityService.rootInActiveWindow
                val list =
                    nodeInfo.findAccessibilityNodeInfosByText("赞")
                for (i in list) {
                    if (i.text.equals("赞") && i.className.equals(CommonTag.TextView)) {
                        CommonToolUtils.performClick(i)
                    }
                }
            }
        }
    }
}