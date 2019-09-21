package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class Oppo : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Oppo

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName>
        get() {
            val rst = ArrayList<ComponentName>()
            rst.addAll(oppoComponentsNamesAutoStartColorOs30)
            rst.addAll(oppoComponentsNamesAutoStartColorOs21)
            rst.addAll(oppoComponentsNamesAutoStartColorOsOlder)
            rst.addAll(oppoComponentsNamesPowerSavingColorOs)
            rst.add(oppoComponentsNamesNotificationColorOs)
            return rst
        }

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        val intentList = ActionUtils.createIntentList(oppoComponentsNamesPowerSavingColorOs)

        return if (ActionUtils.isAtLeastOneIntentAvailable(context.packageManager, intentList))
            KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                intentActionList = intentList
            )
        else
            KillerManagerAction()
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction? {
        var intentList: List<Intent> =
            ActionUtils.createIntentList(oppoComponentsNamesAutoStartColorOs30)
        val packageManager = context.packageManager
        if (ActionUtils.isAtLeastOneIntentAvailable(packageManager, intentList)) {
            return KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = intentList
            )
        }

        intentList = ActionUtils.createIntentList(oppoComponentsNamesAutoStartColorOs21)
        if (ActionUtils.isAtLeastOneIntentAvailable(packageManager, intentList)) {
            return KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = intentList
            )
        }

        intentList = ActionUtils.createIntentList(oppoComponentsNamesAutoStartColorOsOlder)
        return if (ActionUtils.isAtLeastOneIntentAvailable(packageManager, intentList))
            KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = intentList
            )
        else
            KillerManagerAction()
    }

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(
                ActionUtils.createIntent(
                    oppoComponentsNamesNotificationColorOs
                )
            )
        )

    companion object {
        /**
         * java.lang.SecurityException:
         * Permission Denial: starting Intent { cmp=com.coloros.safecenter/.startupapp.StartupAppListActivity } f
         * rom ProcessRecord{7eba0ba 27527:crb.call.follow.mycrm/u0a229} (pid=27527, uid=10229)
         * requires oppo.permission.OppoCOMPONENT_SAFE
         */
        /*    private static final String OppoColorOsNOTIFICATIONPackageR_V4 = "com.android.settings";
    private static final String OppoColorOsNOTIFICATIONActivityV4 = "com.android.settings.applications.InstalledAppDetails";*/

        private const val packageAutoStartColorOs30 = "com.coloros.safecenter"

        private const val packageAutoStartColorOs21 = "com.color.oppoguardelf"

        private const val packageAutoStartColorOsOlder = "com.oppo.safe"

        private val oppoComponentsNamesAutoStartColorOs30 = listOf(
            // STARTUP Coloros >= 3.0
            ComponentName(
                packageAutoStartColorOs30,
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            ),
            ComponentName(
                packageAutoStartColorOs30,
                "com.coloros.safecenter.startupapp.StartupAppListActivity"
            )
        )

        private val oppoComponentsNamesAutoStartColorOs21 = listOf(
            // STARTUP Coloros >= 2.1
            ComponentName(
                packageAutoStartColorOs21,
                "com.color.safecenter.permission.startup.StartupAppListActivity"
            ),
            ComponentName(
                packageAutoStartColorOs21,
                "com.color.safecenter.startupapp.StartupAppListActivity"
            )
        )

        private val oppoComponentsNamesAutoStartColorOsOlder = listOf(
            // STARTUP OLDER VERSION
            ComponentName(
                packageAutoStartColorOsOlder,
                "com.oppo.safe.permission.startup.StartupAppListActivity"
            )
        )

        private const val packagePowerSavingColorOs30 = "com.coloros.oppoguardelf"

        private val oppoComponentsNamesPowerSavingColorOs = listOf(
            // POWER SAVING MODE
            ComponentName(
                packagePowerSavingColorOs30,
                "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity"
            ),
            ComponentName(
                packagePowerSavingColorOs30,
                "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
            )
        )

        private const val oppoColorOsNotificationPackager = "com.coloros.notificationmanager"
        // POWER SAVING MODE
        private val oppoComponentsNamesNotificationColorOs = ComponentName(
            oppoColorOsNotificationPackager,
            "com.coloros.notificationmanager.NotificationCenterActivity"
        )
    }
}
