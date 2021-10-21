package com.example.myaccessibilityservice.service.utils

import android.accessibilityservice.AccessibilityService
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.service.AccessibilitySampleService

class AddFriendUtils {
    val TAG = "AddFriendUtls"
    lateinit var accessibilityService: AccessibilityService

    companion object {
        //是否开启添加好友
        var IS_OPEN_ADD_FRIEND = true

        //从通讯录添加好友后到个人中心，关闭当前页面
        var AGREE_FRIEND_PERSONAL_PAGE = false

        //当前新的朋友访问的位置
        private var ADD_FRIEND_POSITION = 0

        //是否同意添加好友
        private var IS_AGREE_ADD = true

        //是否发送请求，发送后在没跳转至个人详情前不再发出请求
        private var IS_SEED_MESSAGE = true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    constructor(accessibilityService: AccessibilityService, event: AccessibilityEvent) {
        this.accessibilityService = accessibilityService
        addFriends(event)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun addFriends(event: AccessibilityEvent) {
        val eventType = event.eventType
        if (IS_OPEN_ADD_FRIEND) {
            when (eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    var className = event.className
                    when (className) {
                        CommonTag.SEE_PHONE_CONTACTS_PAGE -> {
                            AGREE_FRIEND_PERSONAL_PAGE = true
                            //通过手机联系人添加好友
                            if (event.contentDescription.contains("查看手机通讯录")) {
                                val nodeInfo = accessibilityService.rootInActiveWindow
                                val list =
                                    nodeInfo.findAccessibilityNodeInfosByViewId(CommonTag.SEE_PHONE_CONTACTS_LISTVIEW)
                                if (list.size > 0) {
                                    for (nodeInfo in list) {
                                        var addName =
                                            nodeInfo.findAccessibilityNodeInfosByText("添加")
                                        if (addName.size > 0) {
                                            for (node in addName.withIndex()) {
                                                var nodeValue = node.value
                                                if (ADD_FRIEND_POSITION >= addName.size) {
                                                    IS_AGREE_ADD = false
                                                }
                                                if (IS_AGREE_ADD && ADD_FRIEND_POSITION == node.index) {
                                                    if (nodeValue.text.equals("添加")) {
                                                        CommonToolUtils.performClick(nodeValue.parent)
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        CommonTag.APPLY_FRIENDS_PAGE -> {
                            //进入好友申请页面
                            val nodeInfo = accessibilityService.rootInActiveWindow
                            var nodeList = nodeInfo.findAccessibilityNodeInfosByText("发送")
                            for (node in nodeList) {
                                if (node.className == CommonTag.BUTTON && node.text.equals("发送") && IS_SEED_MESSAGE) {
                                    CommonToolUtils.performClick(node)
                                    Log.i(AccessibilitySampleService.TAG, "=============发送次数")
                                    IS_SEED_MESSAGE = false
                                }
                            }
                        }
                        CommonTag.APPLY_FRIENDS_PASS_PAGE -> {
                            //通过好友认证
                            if (AGREE_FRIEND_PERSONAL_PAGE) {
                                ADD_FRIEND_POSITION++
                                IS_SEED_MESSAGE = true
                                AGREE_FRIEND_PERSONAL_PAGE = false
                                Thread.sleep(1000)
                                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                                Log.i(AccessibilitySampleService.TAG, "=============返回次数")
                            }
                        }
                    }
                }
            }
        }

    }
}