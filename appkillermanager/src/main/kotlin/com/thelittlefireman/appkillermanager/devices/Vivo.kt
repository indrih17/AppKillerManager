package com.thelittlefireman.appkillermanager.devices

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class Vivo : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Vivo

    override val isThatRom: Boolean = false

    // TODO TEST "com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"
    override val componentNameList: List<ComponentName> = listOf(
        componentNames26,
        componentNames30
    )

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = false

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? = null

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        ActionUtils
            .getFirstAvailableActionOrNull(
                packageManager = context.packageManager,
                type = KillerManagerActionType.ActionAutoStart,
                componentNameList = listOf(componentNames26, componentNames30)
            )

    override fun getActionNotification(context: Context): KillerManagerAction? = null

    /**
     * Funtouch OS 2.6 and lower version
     * Funtouch OS 3.0 and higher version
     */
    companion object {
        // PACKAGE
        private const val packageAutoStart26 = "com.iqoo.secure"
        private const val packageAutoStart30 = "com.vivo.permissionmanager"

        // COMPONENT
        private val componentNames26 = ComponentName(
            packageAutoStart26,
            "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
        )// == ACTION com.iqoo.secure.settingwhitelist

        //private final String p1c2 = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"; //java.lang.SecurityException: Permission Denial:
        private val componentNames30 = ComponentName(
            packageAutoStart30,
            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
        )
    }
}
