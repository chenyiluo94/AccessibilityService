package com.example.myaccessibilityservice.service.utils

import android.view.accessibility.AccessibilityNodeInfo

object CommonToolUtils {
    /**
     * 执行具体的点击
     */
    fun performClick(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) {
            return
        }
        if (nodeInfo.isClickable) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            performClick(nodeInfo.parent)
        }
    }
}
