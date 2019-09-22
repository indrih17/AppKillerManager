package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
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
        lenovoComponentNamesNotification
    )

    override val intentActionList: List<String> = emptyList()

    override fun isActionPowerSavingAvailable(context: Context): Boolean = false

    override fun isActionAutoStartAvailable(): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? = null

    override fun getActionAutoStart(context: Context): KillerManagerAction? = null

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(lenovoComponentNamesNotification))
        )

    companion object {
        private const val lenovoPackage = "com.lenovo.systemuiplus"

        private val lenovoComponentNamesNotification = ComponentName(
            lenovoPackage,
            "com.lenovo.systemuiplus.notifymanager.AppNotificationOptimized"
        )
    }
}