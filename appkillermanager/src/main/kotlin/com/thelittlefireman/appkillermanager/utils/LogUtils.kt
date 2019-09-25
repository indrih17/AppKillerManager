package com.thelittlefireman.appkillermanager.utils

import android.content.pm.PackageManager
import android.util.Log
import com.thelittlefireman.appkillermanager.BuildConfig
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.exceptions.IntentNotFoundException
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

object LogUtils {
    var logCustomListener: LogCustomListener? = null

    interface LogCustomListener {
        fun i(tag: String, message: String)
        fun e(tag: String, message: String?, exception: Exception)
    }

    fun i(tag: String, message: String) {
        logCustomListener?.i(tag, message)
        Log.i(tag, message)
    }

    fun e(tag: String, exception: Exception) {
        val logMessage = exception.message
        logCustomListener?.e(tag, logMessage, exception)
        Log.e(tag, logMessage, exception)
    }

    fun intentNotFound(
        packageManager: PackageManager,
        device: DeviceBase,
        extraDebugInfo: String,
        actionType: KillerManagerActionType
    ) =
        e(
            tag = KillerManager::class.java.name,
            exception = IntentNotFoundException(
                message = """
                    INTENT NOT FOUND: $extraDebugInfo,
                    Library version name: ${BuildConfig.VERSION_NAME},
                    Library version code: ${BuildConfig.VERSION_CODE},
                    KillerManagerActionType: ${actionType.name},
                    System utils: ${SystemUtils.defaultDebugInformation},
                    Device: ${device.getExtraDebugInformations(packageManager)}
                """.trimIndent()
            )
        )
}
