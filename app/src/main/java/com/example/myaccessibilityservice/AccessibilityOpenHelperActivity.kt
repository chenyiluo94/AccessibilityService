package com.example.myaccessibilityservice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.utils.AccessibilityUtil
import java.util.*

/**
 * 辅助功能权限打开辅助activity，用于启动辅助功能设置页面
 */
class AccessibilityOpenHelperActivity : Activity() {
    //是否第一次进来
    private var isFirstCome = true
    //计时器
    private var timer: Timer? = null
    //时间任务
    private var timerTask: TimerTask? = null
    private var mTimeoutCounter = 0
    private val TIMEOUT_MAX_INTERVAL = 60 * 2 // 2 min
    private val TIMER_CHECK_INTERVAL: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accessibility_transparent_layout)
        val intent = intent
        if (intent != null && intent.extras != null) {
            val action = intent.getStringExtra(CommonTag.ACTION)
            if (CommonTag.ACTION_FINISH_SELF == action) {
                finishCurrentActivity()
                return
            }
        }
        mTimeoutCounter = 0
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishCurrentActivity()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent?.extras != null) {
            val action = intent.getStringExtra(CommonTag.ACTION)
            if (CommonTag.ACTION_FINISH_SELF == action) {
                finishCurrentActivity()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstCome) {
            removeDelayedToastTask()
            finishCurrentActivity()
        } else {
            jumpActivities()
            startCheckAccessibilityOpen()
        }
        isFirstCome = false
    }

    private fun jumpActivities() {
        try {
            val intent = AccessibilityUtil.getAccessibilitySettingPageIntent(this)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        freeTimeTask()
        super.onDestroy()
    }

    private fun finishCurrentActivity() {
        freeTimeTask()
        finish()
    }

    private fun startCheckAccessibilityOpen() {
        freeTimeTask()
        initTimeTask()
        timer!!.schedule(timerTask, 0, TIMER_CHECK_INTERVAL)
    }

    private fun initTimeTask() {
        timer = Timer()
        mTimeoutCounter = 0
        timerTask = object : TimerTask() {
            override fun run() {
                //判断是否开启辅助功能
                if (AccessibilityUtil.isAccessibilitySettingsOn(this@AccessibilityOpenHelperActivity)) {
                    //关闭计时任务
                    freeTimeTask()
                    Looper.prepare()
                    try {
                        runOnUiThread { Toast.makeText(this@AccessibilityOpenHelperActivity, "辅助功能开启成功", Toast.LENGTH_SHORT).show() }
                        val intent = Intent()
                        intent.putExtra(CommonTag.ACTION, CommonTag.ACTION_FINISH_SELF)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.setClass(this@AccessibilityOpenHelperActivity, this@AccessibilityOpenHelperActivity.javaClass)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Looper.loop()
                }
                // 超过2分钟超时，就释放timer。
                mTimeoutCounter++
                if (mTimeoutCounter > TIMEOUT_MAX_INTERVAL) {
                    freeTimeTask()
                }
            }
        }
    }

    private fun freeTimeTask() {
        if (timerTask != null) {
            timerTask!!.cancel()
            timerTask = null
        }
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    companion object {
        protected var mHandle: Handler? = Handler()
        protected var tipToastDelayRunnable: Runnable? = null
        private fun removeDelayedToastTask() {
            if (mHandle != null && tipToastDelayRunnable != null) {
                mHandle!!.removeCallbacks(tipToastDelayRunnable!!)
            }
        }
    }
}