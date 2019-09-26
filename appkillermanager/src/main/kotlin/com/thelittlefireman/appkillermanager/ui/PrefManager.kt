package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

internal class PrefManager(context: Context) {
    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("run_status", Context.MODE_PRIVATE)
    }

    private val defValue = ProgressOfEliminatingOptimizations.NotStarted.value

    private fun getKey(action: KillerManagerActionType) =
        when (action) {
            KillerManagerActionType.ActionPowerSaving -> "power_saving"
            KillerManagerActionType.ActionAutoStart -> "auto_start"
            KillerManagerActionType.ActionNotifications -> "notification"
        }

    fun setProgressStatus(
        action: KillerManagerActionType,
        status: ProgressOfEliminatingOptimizations
    ) =
        pref.edit { putInt(getKey(action), status.value) }

    fun getProgressStatus(action: KillerManagerActionType): ProgressOfEliminatingOptimizations =
        pref
            .getInt(getKey(action), defValue)
            .takeIf { it != defValue }
            ?.let { convertToProgress(it) }
            ?: ProgressOfEliminatingOptimizations.NotStarted
}