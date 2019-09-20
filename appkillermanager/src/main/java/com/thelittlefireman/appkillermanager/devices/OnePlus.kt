package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class OnePlus : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.OnePlus

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(onePlusComponentNames)

    override val intentActionList: List<String> = emptyList()

    // This is mandatory for new oneplus version android 8
    override fun needToUseAlongWithActionDoseMode(): Boolean = true

    override fun isActionPowerSavingAvailable(context: Context): Boolean = false

    override fun isActionAutoStartAvailable(context: Context): Boolean = true

    override fun isActionNotificationAvailable(context: Context): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction()

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(ActionUtils.createIntent(onePlusComponentNames))
        )

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction()

    companion object {
        private const val onePlusPackage = "com.oneplus.security"
        private val onePlusComponentNames = ComponentName(
            onePlusPackage,
            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
        )
    }
}
