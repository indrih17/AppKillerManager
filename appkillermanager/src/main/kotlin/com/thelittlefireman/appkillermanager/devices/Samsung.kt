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

class Samsung : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Samsung

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        componentNamesPowerSavingV1,
        componentNamesPowerSavingV2,
        componentNamesPowerSavingV3,
        componentNamesMemoryManagerV3
    )

    override val intentActionList: List<String> = intentActions

    // SmartManager is not available before lollipop version,
    // But after the Android Pie is used by One UI.
    override fun isActionPowerSavingAvailable(context: Context): Boolean =
        Build.VERSION.SDK_INT in Build.VERSION_CODES.LOLLIPOP..Build.VERSION_CODES.O_MR1

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        ActionUtils
            .getFirstAvailableActionOrNull(
                packageManager = context.packageManager,
                type = KillerManagerActionType.ActionPowerSaving,
                actionList = listOf(actionPowerSaving),
                componentNameList = listOf(
                    componentNamesPowerSavingV3,
                    componentNamesPowerSavingV2,
                    componentNamesPowerSavingV1
                )
            )

    override fun getActionAutoStart(context: Context): KillerManagerAction? = null

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(action = actionNotification))
        )

    companion object {
        // PACKAGE
        private const val packageMemoryManager = "com.samsung.memorymanager"
        // ANDROID 5.0/5.1
        private const val packagePowerSavingV1 = "com.samsung.android.sm"
        // ANDROID 6.0
        private const val packagePowerSavingV2 = "com.samsung.android.sm_cn"
        // ANDROID 7.0
        private const val packagePowerSavingV3 = "com.samsung.android.lool"

        // ACTION
        // crash "com.samsung.android.lool","com.samsung.android.sm.ui.battery.AppSleepListActivity"
        private const val actionPowerSaving = "com.samsung.android.sm.ACTION_BATTERY"
        private const val actionNotification =
            "com.samsung.android.sm.ACTION_SM_NOTIFICATION_SETTING"
        private const val batteryActivity = "com.samsung.android.sm.ui.battery.BatteryActivity"
        private val intentActions = listOf(
            actionPowerSaving,
            actionNotification
        )

        // ACTION
        // ANDROID 5.0/5.1
        private val componentNamesPowerSavingV1 = ComponentName(
            packagePowerSavingV1,
            batteryActivity
        )
        // ANDROID 6.0
        private val componentNamesPowerSavingV2 = ComponentName(
            packagePowerSavingV2,
            batteryActivity
        )
        // ANDROID 7.0
        private val componentNamesPowerSavingV3 = ComponentName(
            packagePowerSavingV3,
            batteryActivity
        )
        // MEMORY MANAGER (NOT WORKING)
        private val componentNamesMemoryManagerV3 = ComponentName(
            packageMemoryManager,
            "com.samsung.memorymanager.RamActivity"
        )
    }
}
