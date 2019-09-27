package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class Meizu : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Meizu

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName>? = null

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean =
        getMeizuSecVersion(packageManager) != MeizuSecurityCenterVersion.Sec51

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        val packageManager = context.packageManager

        ActionUtils
            .getFirstAvailableActionOrNull(
                packageManager = packageManager,
                type = KillerManagerActionType.ActionPowerSaving,
                actionList = listOf(actionPowerSaving)
            )
            ?.let { return it }

        val powerSavingActivity = when (getMeizuSecVersion(packageManager)) {
            MeizuSecurityCenterVersion.Sec22 -> powerSavingActivityV22
            MeizuSecurityCenterVersion.Sec34 -> powerSavingActivityV34
            MeizuSecurityCenterVersion.Sec37 -> powerSavingActivityV37
            MeizuSecurityCenterVersion.Sec51 -> powerSavingActivityV51
            else -> null
        }

        return KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(
                if (powerSavingActivity != null)
                    ActionUtils
                        .createIntent()
                        .also { it.setClassName(packageDefault, powerSavingActivity) }
                else
                    getDefaultSettingAction(context)
            )
        )
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        when (getMeizuSecVersion(context.packageManager)) {
            MeizuSecurityCenterVersion.Sec51 ->
                null

            else -> KillerManagerAction(
                KillerManagerActionType.ActionAutoStart,
                intentActionList = listOf(getDefaultSettingAction(context))
            )
        }

    private fun getDefaultSettingAction(context: Context): Intent {
        val intent = ActionUtils.createIntent(action = actionAppSpec)
        intent.putExtra("packageName", context.packageName)
        return intent
    }

    override fun getActionNotification(context: Context): KillerManagerAction? {
        val notifActivity = when (getMeizuSecVersion(context.packageManager)) {
            MeizuSecurityCenterVersion.Sec37,
            MeizuSecurityCenterVersion.Sec41 -> notificationActivity

            MeizuSecurityCenterVersion.Sec51 -> notificationSec51

            else -> null
        }
        return KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(
                if (notifActivity != null)
                    ActionUtils.createIntent(
                        componentName = ComponentName(packageDefault, notifActivity)
                    )
                else
                    getDefaultSettingAction(context)
            )
        )
    }

    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        val result = super.getExtraDebugInformations(packageManager)
        val stringBuilder = StringBuilder(result)
            .append("MeizuSecVersionMethod: ")
            .append(getMeizuSecVersion(packageManager))

        try {
            stringBuilder
                .append("\nMeizuSecPackageVersion: ")
                .append(
                    packageManager
                        .getPackageInfo(packageDefault, 0)
                        .versionName
                )
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.e<Meizu>(e)
        }

        // ----- PACKAGE INFORMATIONS -----
        stringBuilder
            .append("\n$actionAppSpec: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionAppSpec
                )
            )

        stringBuilder
            .append("\n$actionPowerSaving: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionPowerSaving
                )
            )

        stringBuilder
            .append("\n$powerSavingActivityV22: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV22)
                )
            )

        stringBuilder
            .append("\n$powerSavingActivityV34: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV34)
                )
            )

        stringBuilder
            .append("\n$powerSavingActivityV37: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV37)
                )
            )

        stringBuilder
            .append("\n$powerSavingActivityV51: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV51)
                )
            )

        stringBuilder
            .append("\n$notificationActivity: ")
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionPowerSaving
                )
            )

        return stringBuilder.toString()
    }

    private enum class MeizuSecurityCenterVersion {
        Sec22, // Meizu security center : 2.2.0922, 2.2.0310
        Sec34, // Meizu security center : 3.4.0316
        Sec36, // Meizu security center : 3.6.0802
        Sec37, // Meizu security center : 3.7.1101
        Sec41, // Meizu security center : 4.1.10
        Sec51  // Meizu security center : 5.1.80
    }

    private fun getMeizuSecVersion(packageManager: PackageManager): MeizuSecurityCenterVersion =
        try {
            val info = packageManager.getPackageInfo(packageDefault, 0)
            val version = info.versionName
            Log.i("Meizu security center: ", version)
            when {
                version.startsWith("2") ->
                    MeizuSecurityCenterVersion.Sec22

                version.startsWith("3") -> {
                    val d = Integer.parseInt(version.substring(2, 3))
                    Log.i("Meizu security center: ", "d: $d")
                    when {
                        d <= 4 -> MeizuSecurityCenterVersion.Sec34
                        d < 7 -> MeizuSecurityCenterVersion.Sec36
                        else -> MeizuSecurityCenterVersion.Sec37
                    }
                }

                version.startsWith("4") ->
                    MeizuSecurityCenterVersion.Sec41

                version.startsWith("5") ->
                    MeizuSecurityCenterVersion.Sec51


                else ->
                    MeizuSecurityCenterVersion.Sec51
            }
        } catch (e: Exception) {
            MeizuSecurityCenterVersion.Sec51
        }

    companion object {
        // PACKAGE
        private const val packageDefault = "com.meizu.safe"

        // ACTION
        private const val actionAppSpec = "com.meizu.safe.security.SHOW_APPSEC"
        private const val actionPowerSaving = "com.meizu.power.PowerAppKilledNotification"

        // ACTIVITY
        private const val powerSavingActivityV22 = "com.meizu.safe.cleaner.RubbishCleanMainActivity"
        private const val powerSavingActivityV34 = "com.meizu.safe.powerui.AppPowerManagerActivity"
        private const val powerSavingActivityV37 =
            "com.meizu.safe.powerui.PowerAppPermissionActivity" // == ACTION com.meizu.power.PowerAppKilledNotification4
        private const val powerSavingActivityV51 = "com.meizu.safe.permission.SmartBGActivity"

        private const val notificationActivity = "com.meizu.safe.permission.NotificationActivity"
        private const val notificationSec51 = "com.meizu.safe.permission.PermissionMainActivity"
    }
}
