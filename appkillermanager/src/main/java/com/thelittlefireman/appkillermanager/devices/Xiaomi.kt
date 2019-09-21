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
        miuiComponentsNamesAutoStart,
        miuiComponentsNamesPowerSave,
        miuiComponentsNamesPowerSaveList
    )

    override val intentActionList: List<String> = listOf(
        miuiActionPowerSaveList,
        miuiActionAutoStartList
    )

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction {
        val intent = ActionUtils.createIntent(action = miuiActionPowerSave)
        intent.putExtra(miuiActionPowerSaveExtraName, context.packageName)
        intent.putExtra(miuiActionPowerSaveExtraLabel, SystemUtils.getApplicationName(context))
        return KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction {
        val intent = ActionUtils.createIntent(miuiComponentsNamesAutoStart)
        intent.putExtra(miuiActionAutoStartExtraName, context.packageName)
        intent.putExtra(miuiActionAutoStartExtraLabel, SystemUtils.getApplicationName(context))
        intent.putExtra(miuiActionAutoStartExtraAction, 3)
        intent.putExtra(miuiActionAutoStartExtraPosition, -1)
        intent.putExtra(miuiActionAutoStartExtraWhiteList, false)
        return KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        var rst = super.getExtraDebugInformations(packageManager)
        rst += miuiVersionNameProperty + miuiRomVersionName
        return rst
    }

    companion object {
        private const val miuiActionPerms = "miui.intent.action.APP_PERM_EDITOR"
        private const val miuiActionPermsExtra = "extra_pkgname"

        // region ------ vars power save
        private const val miuiPackagePowerSave = "com.miui.powerkeeper"
        //  OPEN DEFAULT LIST BATTERY SAVER
        private const val miuiActionPowerSaveList = "miui.intent.action.POWER_HIDE_MODE_APP_LIST"
        private val miuiComponentsNamesPowerSaveList = ComponentName(
            miuiPackagePowerSave,
            "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity"
        ) // == ACTION POWER_HIDE_MODE_APP_LIST

        //  OPEN DEFAULT LIST BATTERY SAVER
        private const val miuiActionPowerSave = "miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY"

        // ONE SPECIFIQUE APP == ACTION miui.intent.action.HIDDEN_APPS_CONFIG_ACTIVITY
        private val miuiComponentsNamesPowerSave = ComponentName(
            miuiPackagePowerSave,
            "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
        )

        private const val miuiActionPowerSaveExtraName = "package_name"
        private const val miuiActionPowerSaveExtraLabel = "package_label"
        // endregion

        // region ------ vars AUTOSTART
        private const val miuiActionAutoStartList = "miui.intent.action.OP_AUTO_START"
        private const val miuiPackageAutoStart = "com.miui.securitycenter"
        private val miuiComponentsNamesAutoStart = ComponentName(
            miuiPackageAutoStart,
            "com.miui.permcenter.autostart.AutoStartManagementActivity"
        )

        private const val miuiActionAutoStartExtraName = "pkg_name"
        private const val miuiActionAutoStartExtraLabel = "pkg_label"
        private const val miuiActionAutoStartExtraAction = "action" // default 3 unknown parameter
        private const val miuiActionAutoStartExtraPosition =
            "pkg_position" // default -1 unknown position
        private const val miuiActionAutoStartExtraWhiteList =
            "white_list" // default need to be false to be handle

        // endregion

        private const val miuiVersionNameProperty = "ro.miui.ui.version.name"

        private val miuiRomVersionName: String
            get() =
                try {
                    SystemUtils.getSystemProperty(miuiVersionNameProperty) ?: ""
                } catch (e: Exception) {
                    LogUtils.e(SystemUtils::class.java.name, e.message)
                    ""
                }
    }

}
