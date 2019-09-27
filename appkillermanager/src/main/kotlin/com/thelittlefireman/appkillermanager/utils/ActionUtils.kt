package com.thelittlefireman.appkillermanager.utils

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

object ActionUtils {
    fun createIntent(
        componentName: ComponentName? = null,
        action: String? = null
    ) =
        Intent().also { intent ->
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            componentName?.let(intent::setComponent)
            action?.let(intent::setAction)
            return@also
        }

    fun createIntentList(componentNameList: List<ComponentName>): List<Intent> =
        componentNameList
            .map { componentName ->
                createIntent(componentName)
            }

    fun getExtrasDebugInformation(intentList: List<Intent>): String =
        if (intentList.isEmpty())
            "intentList is isEmpty"
        else
            intentList.joinToString("\n") { it.component.toString() }

    fun isIntentAvailable(
        packageManager: PackageManager,
        componentName: ComponentName? = null,
        actionIntent: String? = null
    ): Boolean =
        isIntentAvailable(packageManager, createIntent(componentName, actionIntent))

    fun filterAvailableIntents(
        packageManager: PackageManager,
        intentList: List<Intent>
    ): List<Intent> =
        intentList.filter { isIntentAvailable(packageManager, it) }

    fun getFirstAvailableActionOrNull(
        packageManager: PackageManager,
        type: KillerManagerActionType,
        actionList: List<String> = emptyList(),
        componentNameList: List<ComponentName> = emptyList()
    ): KillerManagerAction? {
        actionList
            .map { createIntent(action = it) }
            .firstOrNull { isIntentAvailable(packageManager, it) }
            ?.let { return KillerManagerAction(type, listOf(it)) }

        componentNameList
            .map { createIntent(componentName = it) }
            .firstOrNull { isIntentAvailable(packageManager, it) }
            ?.let { return KillerManagerAction(type, listOf(it)) }

        return null
    }

    fun getFirstAvailableActionOrNull(
        packageManager: PackageManager,
        type: KillerManagerActionType,
        vararg componentNameList: List<ComponentName>
    ): KillerManagerAction? {
        componentNameList
            .map { createIntentList(it) }
            .firstOrNull { isAtLeastOneIntentAvailable(packageManager, it) }
            ?.let { return KillerManagerAction(type, it) }

        return null
    }

    fun isAtLeastOneIntentAvailable(
        packageManager: PackageManager,
        intentList: List<Intent>
    ): Boolean =
        intentList.firstOrNull { isIntentAvailable(packageManager, it) } != null

    private fun isIntentAvailable(packageManager: PackageManager, intent: Intent): Boolean {
        val available = packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .isNotEmpty()
        if (!available)
            LogUtils.intentNotAvailable(intent)
        return available
    }
}
