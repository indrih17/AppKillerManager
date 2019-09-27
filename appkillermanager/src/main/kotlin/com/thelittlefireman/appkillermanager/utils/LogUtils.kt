package com.thelittlefireman.appkillermanager.utils

object LogUtils {
    var logCustomListener: ((tag: String, message: String?, exception: Exception) -> Unit)? = null

    inline fun <reified T> e(exception: Exception, message: String? = null) {
        logCustomListener?.invoke(T::class.java.name, message, exception)
    }
}
