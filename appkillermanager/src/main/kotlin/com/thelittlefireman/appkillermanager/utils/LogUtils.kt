package com.thelittlefireman.appkillermanager.utils

import android.content.Intent

object LogUtils {
    var logCustomListener: LogListener? = null

    interface LogListener {
        fun e(tag: String, message: String?, exception: Exception)
        fun intentNotAvailable(intent: Intent)
    }

    inline fun <reified T> e(exception: Exception, message: String? = null) {
        logCustomListener?.e(T::class.java.name, message, exception)
    }

    fun intentNotAvailable(intent: Intent) {
        logCustomListener?.intentNotAvailable(intent)
    }
}
