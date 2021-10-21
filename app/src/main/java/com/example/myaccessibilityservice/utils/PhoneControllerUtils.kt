package com.example.myaccessibilityservice.utils

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager

object PhoneControllerUtils {

    /**
     * 判断是否锁屏
     * @param context
     * @return
     */
    fun isLockScreen(context: Context): Boolean {
        val km =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km.inKeyguardRestrictedInputMode()
    }

    /**
     * 唤醒手机并解锁(不能有锁屏密码)
     * @param context
     */
    @SuppressLint("InvalidWakeLockTag")
    fun wakeAndUnlockScreen(context: Context) {
        val pm =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP
                    or PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright"
        )
        wakeLock.acquire(1000) // 点亮屏幕
        wakeLock.release()
        val km =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val lock = km.newKeyguardLock("unlock")
        lock.disableKeyguard() // 解锁
    }
}