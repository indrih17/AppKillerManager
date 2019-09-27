package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit

internal class PrefManager(context: Context) {
    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("run_status", Context.MODE_PRIVATE)
    }

    private val defValue = ProgressOfEliminatingOptimizations.NotStarted.value

    private val Intent.name: String
        inline get() = component.toString()

    fun getProgressStatusList(
        intentList: List<Intent>
    ): List<Pair<Intent, ProgressOfEliminatingOptimizations>> =
        intentList.map { it to getProgressStatus(it) }

    private fun getProgressStatus(intent: Intent): ProgressOfEliminatingOptimizations =
        pref
            .getInt(intent.name, defValue)
            .takeIf { it != defValue }
            ?.let { convertToProgress(it) }
            ?: ProgressOfEliminatingOptimizations.NotStarted

    fun setProgressStatusList(
        intentList: List<Intent>,
        status: ProgressOfEliminatingOptimizations
    ) =
        pref.edit {
            for (intent in intentList)
                putInt(intent.name, status.value)
        }
}