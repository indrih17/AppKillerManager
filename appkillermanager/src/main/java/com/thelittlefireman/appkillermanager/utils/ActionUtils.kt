package com.thelittlefireman.appkillermanager.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.thelittlefireman.appkillermanager.models.KillerManagerAction

object ActionUtils {
    fun createIntent(
        componentName: ComponentName? = null,
        action: String? = null,
        data: Uri? = null
    ) =
        Intent().also { intent ->
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            componentName?.let(intent::setComponent)
            action?.let(intent::setAction)
            data?.let(intent::setData)
            return@also
        }

    fun createIntentList(componentNameList: List<ComponentName>): List<Intent> =
        componentNameList
            .map { componentName ->
                createIntent(componentName)
            }

    fun getExtrasDebugInformationsWithKillerManagerAction(
        killerManagerActionList: List<KillerManagerAction>
    ): String {
        val stringBuilder = StringBuilder()
        for (action in killerManagerActionList) {
            stringBuilder.append(getExtrasDebugInformations(action.intentActionList))
        }
        return stringBuilder.toString()
    }

    fun getExtrasDebugInformations(intentList: List<Intent>): String {
        val stringBuilder = StringBuilder()
        if (intentList.isEmpty())
            stringBuilder.append("intentList is isEmpty")
        else
            stringBuilder.append("intent is null")
        return stringBuilder.toString()
    }

    fun isIntentAvailable(
        ctx: Context,
        componentName: ComponentName? = null,
        actionIntent: String? = null
    ): Boolean =
        isIntentAvailable(ctx, createIntent(componentName, actionIntent))

    fun isAtLeastOneIntentAvailable(
        ctx: Context,
        killerManagerAction: KillerManagerAction
    ): Boolean =
        isAtLeastOneIntentAvailable(ctx, killerManagerAction.intentActionList)

    fun isAtLeastOneIntentAvailable(ctx: Context, intentList: List<Intent>): Boolean =
        intentList.firstOrNull { isIntentAvailable(ctx, it) } != null

    fun isIntentAvailable(context: Context, intent: Intent): Boolean =
        context
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .isNotEmpty()
}
