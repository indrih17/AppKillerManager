package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class HTC : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Htc

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(componentNamePowerSaving)

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(ActionUtils.createIntent(componentNamePowerSaving))
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun needToUseAlongWithActionDoseMode(): Boolean = true

    companion object {
        // PACKAGE
        private const val packagePitroad = "com.htc.pitroad"

        // COMPONENT
        private val componentNamePowerSaving = ComponentName(
            packagePitroad,
            "com.htc.pitroad.landingpage.activity.LandingPageActivity"
        )
    }
}
