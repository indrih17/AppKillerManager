package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class Letv : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Letv

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        letvComponentNamesAutoStart,
        letvComponentNamesPowerSave
    )

    override val intentActionList: List<String> = emptyList()

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(ActionUtils.createIntent(letvComponentNamesPowerSave))
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(ActionUtils.createIntent(letvComponentNamesAutoStart))
        )

    override fun getActionNotification(context: Context): KillerManagerAction? = null

    companion object {
        // PACKAGE
        private const val packageDef = "com.letv.android.letvsafe"

        // COMPONENT
        private val letvComponentNamesPowerSave = ComponentName(
            packageDef,
            "com.letv.android.letvsafe.BackgroundAppManageActivity"
        )
        private val letvComponentNamesAutoStart = ComponentName(
            packageDef,
            "com.letv.android.letvsafe.AutobootManageActivity"
        )
    }
}
