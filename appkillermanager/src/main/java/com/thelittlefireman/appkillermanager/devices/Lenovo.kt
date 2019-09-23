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

class Lenovo : DeviceAbstract() {
    // TODO NOT SUR IT WORKS ON VIBE UI 3

    override val deviceManufacturer: Manufacturer = Manufacturer.Lenovo

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        componentNamesNotification
    )

    override val intentActionList: List<String> = emptyList()

    override fun isActionPowerSavingAvailable(context: Context): Boolean =
        super.isActionDozeModeNotNecessary(context)

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        super.getActionDozeMode(context)

    override fun getActionAutoStart(context: Context): KillerManagerAction? = null

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(componentNamesNotification))
        )

    companion object {
        // PACKAGE
        private const val packageNotification = "com.lenovo.systemuiplus"

        // COMPONENT
        private val componentNamesNotification = ComponentName(
            packageNotification,
            "com.lenovo.systemuiplus.notifymanager.AppNotificationOptimized"
        )
    }
}