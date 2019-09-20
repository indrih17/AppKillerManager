package com.thelittlefireman.appkillermanager.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object KillerManagerUtils {
    private const val dontShowAgainKey = "DONT_SHOW_AGAIN"
    private fun getSharedPreferences(mContext: Context): SharedPreferences =
        mContext.getSharedPreferences("KillerManager", MODE_PRIVATE)

    /**
     * Set for a specifique actions that we dont need to show the popupAgain
     *
     * @param mContext
     * @param enable
     */
    fun setDontShowAgain(mContext: Context, enable: Boolean) =
        getSharedPreferences(mContext)
            .edit { putBoolean(dontShowAgainKey, enable) }

    fun isDontShowAgain(mContext: Context): Boolean =
        getSharedPreferences(mContext).getBoolean(dontShowAgainKey, false)
}
