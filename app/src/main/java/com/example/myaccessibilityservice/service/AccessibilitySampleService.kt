package com.example.myaccessibilityservice.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.service.utils.*


/**
 * AccessibilityService服务
 */
class AccessibilitySampleService : AccessibilityService() {
    companion object {
        val TAG = "AccessibilityService"
        var node: AccessibilityNodeInfo? = null
        lateinit var jumpEnumType: String
    }

    //服务开启时会调用,
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "onServiceConnected:---------------")
    }

    override fun onCreate() {
        super.onCreate()
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                jumpEnumType = intent?.getStringExtra("JumpEnumType").toString()

                Log.i(TAG, "===========4" + jumpEnumType)
            }
        }
        registerReceiver(broadcast, IntentFilter("com.example.myaccessibilityservice.receiver"))
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.i(TAG, "event:--------------- $event")
        when (jumpEnumType) {
            CommonTag.ADD_FRIEND -> {
                //添加好友
                AddFriendUtils(this, event)
            }
            CommonTag.AUTO_REPLAY -> {
                //自动回复
                AutoReplayUtils(this, event)
            }
            CommonTag.CIRCLE_CLICK -> {
                //点击朋友圈
                CircleClickUtils(this, event)
            }
            CommonTag.CIRCLE_CLICK2 -> {
                //自动点赞
                CircleClickUtils2(this, event)
            }
            CommonTag.RED_ENVELOPES -> {
                //自动抢红包
                RedEnvelopesUtils(this, event)
            }
            CommonTag.OTHER -> {
                //测试其它
                Toast.makeText(this, "测试其他", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "没有传入指定信息", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 连接断开时调用的方法
     */
    override fun onInterrupt() {
        Log.i("AccessibilityService", "onInterrupt:---------------")
    }
}