package com.thelittlefireman.appkillermanager.utils

import android.content.pm.PackageManager
import android.util.Log
import com.thelittlefireman.appkillermanager.BuildConfig
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

object LogUtils {
    var logCustomListener: LogCustomListener? = null

    interface LogCustomListener {
        fun i(tag: String, message: String)
        fun w(tag: String, message: String)
        fun e(tag: String, message: String?)
    }

    fun i(tag: String, message: String) {
        logCustomListener?.i(tag, message)
        Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
        logCustomListener?.w(tag, message)
        Log.w(tag, message)
    }

    fun e(tag: String, message: String?) {
        logCustomListener?.e(tag, message)
        Log.e(tag, message ?: "")
    }

    fun indentNotFound(
        packageManager: PackageManager,
        device: DeviceBase,
        extraDebugInfo: String,
        actionType: KillerManagerActionType
    ) =
        e(
            KillerManager::class.java.name,
            """
                INTENT NOT FOUND: $extraDebugInfo,
                Library version name: ${BuildConfig.VERSION_NAME},
                Library version code: ${BuildConfig.VERSION_CODE},
                KillerManagerActionType: ${actionType.name},
                System utils: ${SystemUtils.defaultDebugInformation},
                Device: ${device.getExtraDebugInformations(packageManager)}
            """.trimIndent()
        )
}
