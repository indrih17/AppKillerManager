package com.thelittlefireman.appkillermanager.devices

import android.content.ComponentName
import android.content.Context
import android.content.Intent
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

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getActionAutoStart(context: Context): KillerManagerAction? {
        var intent: Intent = ActionUtils.createIntent(componentNames26)
        val packageManager = context.packageManager
        if (ActionUtils.isIntentAvailable(packageManager, intent))
            return KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = listOf(intent)
            )

        intent = ActionUtils.createIntent(componentNames30)
        return if (ActionUtils.isIntentAvailable(packageManager, intent))
            KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = listOf(intent)
            )
        else
            null
    }

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

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
