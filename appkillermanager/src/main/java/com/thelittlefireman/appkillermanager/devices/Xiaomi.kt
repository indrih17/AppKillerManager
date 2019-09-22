package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer
import com.thelittlefireman.appkillermanager.utils.SystemUtils

class Xiaomi : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Xaiomi

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        componentsNamesAutoStart,
        componentsNamesPowerSave,
        componentsNamesPowerSaveList
    )

    override val intentActionList: List<String> = listOf(
        actionPowerSaveList,
        actionAutoStartList
    )

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction {
        val intent = ActionUtils.createIntent(action = actionPowerSave)
        intent.putExtra(actionPowerSaveExtraName, context.packageName)
        intent.putExtra(actionPowerSaveExtraLabel, SystemUtils.getApplicationName(context))
        return KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction {
        val intent = ActionUtils.createIntent(componentsNamesAutoStart)
        intent.putExtra(actionAutoStartExtraName, context.packageName)
        intent.putExtra(actionAutoStartExtraLabel, SystemUtils.getApplicationName(context))
        intent.putExtra(actionAutoStartExtraAction, 3)
        intent.putExtra(actionAutoStartExtraPosition, -1)
        intent.putExtra(actionAutoStartExtraWhiteList, false)
        return KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        var rst = super.getExtraDebugInformations(packageManager)
        rst += versionNameProperty + miuiRomVersionName
        return rst
    }

    companion object {
        // PACKAGE
        private const val packagePowerSave = "com.miui.powerkeeper"
        private const val packageAutoStart = "com.miui.securitycenter"

        // ACTION
        private const val actionPowerSave = "miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY"
        private const val actionPowerSaveList = "miui.intent.action.POWER_HIDE_MODE_APP_LIST"
        private const val actionAutoStartList = "miui.intent.action.OP_AUTO_START"
        private const val actionPerms = "miui.intent.action.APP_PERM_EDITOR"
        private const val actionPermsExtra = "extra_pkgname"
        private const val actionPowerSaveExtraName = "package_name"
        private const val actionPowerSaveExtraLabel = "package_label"
        private const val actionAutoStartExtraName = "pkg_name"
        private const val actionAutoStartExtraLabel = "pkg_label"
        private const val actionAutoStartExtraAction = "action" // default 3 unknown parameter
        private const val actionAutoStartExtraPosition =
            "pkg_position" // default -1 unknown position
        private const val actionAutoStartExtraWhiteList =
            "white_list" // default need to be false to be handle

        // COMPONENT
        private val componentsNamesPowerSaveList = ComponentName(
            packagePowerSave,
            "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity"
        ) // == ACTION POWER_HIDE_MODE_APP_LIST
        // ONE SPECIFIQUE APP == ACTION miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY
        private val componentsNamesPowerSave = ComponentName(
            packagePowerSave,
            "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
        )
        private val componentsNamesAutoStart = ComponentName(
            packageAutoStart,
            "com.miui.permcenter.autostart.AutoStartManagementActivity"
        )

        // VERSION
        private const val versionNameProperty = "ro.miui.ui.version.name"
        private val miuiRomVersionName: String
            get() =
                try {
                    SystemUtils.getSystemProperty(versionNameProperty) ?: ""
                } catch (e: Exception) {
                    LogUtils.e(SystemUtils::class.java.name, e.message)
                    ""
                }
    }
}
