package com.example.myaccessibilityservice

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myaccessibilityservice.common.CommonTag
import com.example.myaccessibilityservice.utils.AccessibilityUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var height: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val metric = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(metric)
        height = metric.heightPixels
        onClick()
    }

    private fun onClick() {
        llContactsAdd.setOnClickListener(this)
        llCircleClick.setOnClickListener(this)
        llOther.setOnClickListener(this)
        llAutoReplay.setOnClickListener(this)
        llCircleClick1.setOnClickListener(this)
        llRedEnvelopes.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llContactsAdd -> jumpToContactsAdd(
                CommonTag.ADD_FRIEND,
                tvContactsAdd.text.toString()
            )
            R.id.llCircleClick -> jumpToContactsAdd(
                CommonTag.CIRCLE_CLICK2,
                tvCircleClick.text.toString()
            )
            R.id.llOther -> jumpToContactsAdd(CommonTag.OTHER, tvOther.text.toString())
            R.id.llAutoReplay -> jumpToContactsAdd(
                CommonTag.AUTO_REPLAY,
                tvAutoReplay.text.toString()
            )
            R.id.llCircleClick1 -> jumpToContactsAdd(
                CommonTag.CIRCLE_CLICK,
                tvCircleClick1.text.toString()
            )
            R.id.llRedEnvelopes -> jumpToContactsAdd(
                CommonTag.RED_ENVELOPES,
                tvRedEnvelopes.text.toString()
            )
        }
    }

    private fun jumpToContactsAdd(jumpEnumType: String, text: String) =
        if (AccessibilityUtil.isAccessibilitySettingsOn(this)) {
            Toast.makeText(this, "辅助功能开启成功", Toast.LENGTH_SHORT).show()
            val intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
            startActivity(intent)
            sendBroadcast(Intent().apply {
                action = "com.example.myaccessibilityservice.receiver"
                putExtra("JumpEnumType", jumpEnumType)
            })
        } else {
            val intent = Intent(this, AccessibilityOpenHelperActivity::class.java)
            intent.putExtra(CommonTag.ACTION, CommonTag.ACTION_START_ACCESSIBILITY_SETTING)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

