package com.thelittlefireman.appkillermanager.utils

object LogUtils {
    var logCustomListener: ((tag: String, message: String?, exception: Exception) -> Unit)? = null

    fun e(tag: String, exception: Exception) {
        logCustomListener?.invoke(tag, exception.message, exception)
    }
}
