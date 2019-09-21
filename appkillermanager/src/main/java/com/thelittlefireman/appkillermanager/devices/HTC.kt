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

    override val componentNameList: List<ComponentName> = listOf(htcComponentNamePowerSaving)

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(ActionUtils.createIntent(htcComponentNamePowerSaving))
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun needToUseAlongWithActionDoseMode(): Boolean = true

    companion object {
        private const val htcPitroadPackageName = "com.htc.pitroad"
        private val htcComponentNamePowerSaving = ComponentName(
            htcPitroadPackageName,
            "com.htc.pitroad.landingpage.activity.LandingPageActivity"
        )
    }
}
