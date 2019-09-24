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

class Asus : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Asus

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        componentNameAutoStart,
        componentNameNotification
    )

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean =
        super.isActionDozeModeNotNecessary(context)

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = true

    // Just need to use the regular battery non optimization permission =)
    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        super.getActionDozeMode(context)

    override fun getActionAutoStart(context: Context): KillerManagerAction? {
        val intent = ActionUtils.createIntent(componentNameAutoStart)
        intent.putExtra("showNotice", true)
        return KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionNotification(context: Context): KillerManagerAction? {
        // Need to click on notifications items
        val intent = ActionUtils.createIntent(componentNameNotification)
        intent.putExtra("showNotice", true)
        return KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(intent)
        )
    }

    companion object {
        // PACKAGE
        private const val packageMobileManager = "com.asus.mobilemanager"

        // COMPONENT
        private val componentNameNotification = ComponentName(
            packageMobileManager,
            "com.asus.mobilemanager.entry.FunctionActivity"
        )

        private val componentNameAutoStart = ComponentName(
            packageMobileManager,
            "com.asus.mobilemanager.autostart.AutoStartActivity"
        )
    }
}