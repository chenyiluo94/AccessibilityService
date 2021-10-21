package com.example.myaccessibilityservice.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import com.example.myaccessibilityservice.service.AccessibilitySampleService

/**
 * 辅助功能相关检查的帮助类
 */
object AccessibilityUtil {
    private val ACCESSIBILITY_SERVICE_PATH = AccessibilitySampleService::class.java.canonicalName

    /**
     * 判断是否有辅助功能权限
     *
     * @param context
     * @return
     */
    fun isAccessibilitySettingsOn(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        val packageName = context.packageName
        val serviceStr = "$packageName/$ACCESSIBILITY_SERVICE_PATH"
        Log.i("AccessibilityService","serviceStr:--------------- $serviceStr")
        if (accessibilityEnabled == 1) {
            val mStringColonSplitter = SimpleStringSplitter(':')
            val settingValue = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessabilityService = mStringColonSplitter.next()
                    if (accessabilityService.equals(serviceStr, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getAccessibilitySettingPageIntent(context: Context?): Intent {
        // 一些品牌的手机可能不是这个Intent,需要适配
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }
}