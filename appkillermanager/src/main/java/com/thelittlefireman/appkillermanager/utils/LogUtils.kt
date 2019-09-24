package com.thelittlefireman.appkillermanager.utils

import android.content.pm.PackageManager
import android.util.Log
import com.thelittlefireman.appkillermanager.BuildConfig
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

object LogUtils {
    private var slogCustomListener: LogCustomListener? = null

    fun registerLogCustomListener(logCustomListener: LogCustomListener) {
        slogCustomListener = logCustomListener
    }

    interface LogCustomListener {
        fun i(tag: String, message: String)
        fun w(tag: String, message: String)
        fun e(tag: String, message: String?)
    }

    fun i(tag: String, message: String) {
        slogCustomListener?.i(tag, message)
        Log.i(tag, message)
        //HyperLog.i(tag,message)
    }

    fun w(tag: String, message: String) {
        slogCustomListener?.w(tag, message)
        Log.w(tag, message)
    }

    fun e(tag: String, message: String?) {
        slogCustomListener?.e(tag, message)
        Log.e(tag, message ?: "")
        //HyperLog.e(tag,message)
    }

    fun indentNotFound(
        packageManager: PackageManager,
        device: DeviceBase,
        extraDebugInfo: String,
        actionType: KillerManagerActionType
    ) =
        e(
            KillerManager::class.java.name, "INTENT NOT FOUND: " +
                    extraDebugInfo +
                    ", LibraryVersionName: " + BuildConfig.VERSION_NAME +
                    ", LibraryVersionCode: " + BuildConfig.VERSION_CODE +
                    ", KillerManagerActionType \n" + actionType.name + " SYSTEM UTILS \n" +
                    SystemUtils.defaultDebugInformation + " DEVICE \n" +
                    device.getExtraDebugInformations(packageManager)
        )
}
