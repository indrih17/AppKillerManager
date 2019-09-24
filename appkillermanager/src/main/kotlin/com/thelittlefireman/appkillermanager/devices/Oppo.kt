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
            rst.addAll(componentsNamesAutoStartColorOs30)
            rst.addAll(componentsNamesAutoStartColorOs21)
            rst.addAll(componentsNamesAutoStartColorOsOlder)
            rst.addAll(componentsNamesPowerSavingColorOs)
            rst.add(componentNamesNotificationColorOs)
            return rst
        }

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        val intentList = ActionUtils.createIntentList(componentsNamesPowerSavingColorOs)

        return if (ActionUtils.isAtLeastOneIntentAvailable(context.packageManager, intentList))
            KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                intentActionList = intentList
            )
        else
            KillerManagerAction()
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        ActionUtils.getFirstAvailableActionOrNull(
            context.packageManager,
            KillerManagerActionType.ActionAutoStart,
            componentsNamesAutoStartColorOs30,
            componentsNamesAutoStartColorOs21,
            componentsNamesAutoStartColorOsOlder
        )

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(
                ActionUtils.createIntent(
                    componentNamesNotificationColorOs
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

        // PACKAGE
        private const val packageAutoStartColorOsOlder = "com.oppo.safe"
        private const val packageAutoStartColorOs21 = "com.color.oppoguardelf"
        private const val packageAutoStartColorOs30 = "com.coloros.safecenter"
        private const val packagePowerSavingColorOs30 = "com.coloros.oppoguardelf"
        private const val packageNotification = "com.coloros.notificationmanager"

        // COMPONENT
        private val componentNamesNotificationColorOs = ComponentName(
            packageNotification,
            "com.coloros.notificationmanager.NotificationCenterActivity"
        )

        // LIST OF COMPONENTS
        private val componentsNamesAutoStartColorOsOlder = listOf(
            // STARTUP OLDER VERSION
            ComponentName(
                packageAutoStartColorOsOlder,
                "com.oppo.safe.permission.startup.StartupAppListActivity"
            )
        )
        private val componentsNamesAutoStartColorOs21 = listOf(
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
        private val componentsNamesAutoStartColorOs30 = listOf(
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
        private val componentsNamesPowerSavingColorOs = listOf(
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
    }
}
