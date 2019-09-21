package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import com.thelittlefireman.appkillermanager.R
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

    @DrawableRes
    private val helpImagePowerSaving: Int = R.drawable.samsung

    override val componentNameList: List<ComponentName> = listOf(
        samsungComponentNamesPowerSavingV1,
        samsungComponentNamesPowerSavingV2,
        samsungComponentNamesPowerSavingV3,
        samsungComponentNamesMemoryManagerV3
    )

    override val intentActionList: List<String> = samsungIntentActions

    // SmartManager is not available before lollipop version
    override fun isActionPowerSavingAvailable(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    override fun isActionAutoStartAvailable(): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = false

    override fun needToUseAlongWithActionDoseMode(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        val packageManager = context.packageManager
        var intent = ActionUtils.createIntent(action = samsungActionPowerSaving)
        if (ActionUtils.isIntentAvailable(packageManager, intent))
            return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                helpImages = listOf(helpImagePowerSaving),
                intentActionList = listOf(intent)
            )

        // reset
        intent = ActionUtils.createIntent(samsungComponentNamesPowerSavingV3)
        if (ActionUtils.isIntentAvailable(packageManager, intent))
            return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                helpImages = listOf(helpImagePowerSaving),
                intentActionList = listOf(intent)
            )

        intent = ActionUtils.createIntent(samsungComponentNamesPowerSavingV2)
        if (ActionUtils.isIntentAvailable(packageManager, intent))
            return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                helpImages = listOf(helpImagePowerSaving),
                intentActionList = listOf(intent)
            )

        intent = ActionUtils.createIntent(samsungComponentNamesPowerSavingV1)
        return if (ActionUtils.isIntentAvailable(packageManager, intent))
            KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                helpImages = listOf(helpImagePowerSaving),
                intentActionList = listOf(intent)
            )
        else
            KillerManagerAction()
    }

    // FIXME
    override fun getActionAutoStart(context: Context): KillerManagerAction? = null

    // FIXME : NOTWORKOING NEED PERMISSIONS SETTINGS OR SOMETHINGS ELSE
    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(action = samsungActionNotification))
        )

    companion object {
        // crash "com.samsung.android.lool","com.samsung.android.sm.ui.battery.AppSleepListActivity"
        private const val samsungActionPowerSaving = "com.samsung.android.sm.ACTION_BATTERY"
        private const val samsungActionNotification =
            "com.samsung.android.sm.ACTION_SM_NOTIFICATION_SETTING"
        private const val packageMemoryManager = "com.samsung.memorymanager"
        private const val batteryActivity = "com.samsung.android.sm.ui.battery.BatteryActivity"

        // ANDROID 7.0
        private const val samsungPowerSavingPackageV3 = "com.samsung.android.lool"

        // ANDROID 6.0
        private const val samsungPowerSavingPackageV2 = "com.samsung.android.sm_cn"

        // ANDROID 5.0/5.1
        private const val samsungPowerSavingPackageV1 = "com.samsung.android.sm"

        // ANDROID 5.0/5.1
        private val samsungComponentNamesPowerSavingV1 = ComponentName(
            samsungPowerSavingPackageV1,
            batteryActivity
        )
        // ANDROID 6.0
        private val samsungComponentNamesPowerSavingV2 = ComponentName(
            samsungPowerSavingPackageV2,
            batteryActivity
        )
        // ANDROID 7.0
        private val samsungComponentNamesPowerSavingV3 = ComponentName(
            samsungPowerSavingPackageV3,
            batteryActivity
        )

        // MEMORY MANAGER (NOT WORKING)
        private val samsungComponentNamesMemoryManagerV3 =
            ComponentName(packageMemoryManager, "com.samsung.memorymanager.RamActivity")

        private val samsungIntentActions = listOf(
            samsungActionPowerSaving,
            samsungActionNotification
        )
    }
}
