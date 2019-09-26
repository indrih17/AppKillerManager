package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.alert(
    message: Int,
    title: Int? = null,
    positiveButtonText: Int = android.R.string.ok,
    negativeButtonText: Int = android.R.string.cancel,
    onOkButtonClick: (() -> Unit)?,
    onNoButtonClick: (() -> Unit)? = null,
    windowFeature: Int? = null,
    cancelable: Boolean,
    themeRes: Int = 0
) {
    AlertDialog
        .Builder(this, themeRes)
        .also { alert ->
            alert.setMessage(message)
            title?.let(alert::setTitle)

            onOkButtonClick?.let {
                alert.setPositiveButton(positiveButtonText) { _, _ ->
                    it.invoke()
                }
            }
            onNoButtonClick?.let {
                alert.setNegativeButton(negativeButtonText) { _, _ ->
                    it.invoke()
                }
            }
            alert.setCancelable(cancelable)
        }
        .create()
        .also {
            windowFeature?.let(it::requestWindowFeature)
        }
        .show()
}