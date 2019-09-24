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

class OnePlus : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.OnePlus

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        componentNamesAutoStart,
        componentNamePowerSaving1,
        componentNamePowerSaving2
    )

    override val intentActionList: List<String> = emptyList()

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(
                ActionUtils.createIntent(componentNamePowerSaving1),
                ActionUtils.createIntent(componentNamePowerSaving2)
            )
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(ActionUtils.createIntent(componentNamesAutoStart))
        )

    override fun getActionNotification(context: Context): KillerManagerAction? = null

    companion object {
        // PACKAGE
        private const val packageAuthStart = "com.oneplus.security"

        // COMPONENT
        private val componentNamesAutoStart = ComponentName(
            packageAuthStart,
            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
        )

        private val componentNamePowerSaving1 = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$BgOptimizeAppListActivity"
        )
        private val componentNamePowerSaving2 = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$BgOptimizeSwitchActivity"
        )
    }
}
