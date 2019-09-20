package com.thelittlefireman.appkillermanager.utils

import android.util.Log

object LogUtils {
    private var slogCustomListener: LogCustomListener? = null

    fun registerLogCustomListener(logCustomListener: LogCustomListener) {
        slogCustomListener = logCustomListener
    }

    interface LogCustomListener {
        fun i(tag: String, message: String)
        fun e(tag: String, message: String?)
    }

    fun i(tag: String, message: String) {
        slogCustomListener?.i(tag, message)
        Log.i(tag, message)
        //HyperLog.i(tag,message)
    }

    fun e(tag: String, message: String?) {
        slogCustomListener?.e(tag, message)
        Log.e(tag, message ?: "")
        //HyperLog.e(tag,message)
    }
}
