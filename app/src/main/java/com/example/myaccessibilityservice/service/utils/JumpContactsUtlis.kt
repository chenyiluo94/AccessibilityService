package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.service.AccessibilitySampleService

class JumpContactsUtlis {
    companion object {
        val TAG = "JumpContactsUtlis"
        var ISJUMPTXL = true
    }

    var accessibilityService: AccessibilityService

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(
        accessibilityService: AccessibilityService,
        event: AccessibilityEvent,
        text: String
    ) {
        this.accessibilityService = accessibilityService
        jumpContacts(event, text)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun jumpContacts(event: AccessibilityEvent, text: String) {
        var accessibilityNodeinfo = event.source
        if (accessibilityNodeinfo != null) {
            var list = accessibilityNodeinfo.findAccessibilityNodeInfosByText(text)
            for (node in list) {
                Log.i(AccessibilitySampleService.TAG, "node:--------------- $node")
                if (node.className.equals(CommonTag.TextView) && ISJUMPTXL) {
                    CommonToolUtils.performClick(node)
                    ISJUMPTXL = false
                }
            }
        }
    }
}