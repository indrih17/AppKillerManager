package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
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

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        var intent = ActionUtils.createIntent(action = actionPowerSaving)

        val packageManager = context.packageManager
        if (ActionUtils.isIntentAvailable(packageManager, intent)) {
            return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                intentActionList = listOf(intent)
            )
        }

        intent = ActionUtils.createIntent()
        when (getMeizuSecVersion(packageManager)) {
            MeizuSecurityCenterVersion.Sec22 ->
                intent.setClassName(packageDefault, powerSavingActivityV22)

            MeizuSecurityCenterVersion.Sec34 ->
                intent.setClassName(packageDefault, powerSavingActivityV34)

            MeizuSecurityCenterVersion.Sec37 ->
                intent.setClassName(packageDefault, powerSavingActivityV37)

            else -> return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                intentActionList = listOf(getDefaultSettingAction(context))
            )
        }
        return KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(intent)
        )
    }

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(getDefaultSettingAction(context))
        )

    private fun getDefaultSettingAction(context: Context): Intent {
        val intent = ActionUtils.createIntent(action = actionAppSpec)
        intent.putExtra("packageName", context.packageName)
        return intent
    }

    override fun getActionNotification(context: Context): KillerManagerAction? {
        val intent = ActionUtils.createIntent()
        return when (getMeizuSecVersion(context.packageManager)) {
            MeizuSecurityCenterVersion.Sec37, MeizuSecurityCenterVersion.Sec41 -> {
                intent.component = ComponentName(packageDefault, notificationActivity)
                KillerManagerAction(
                    KillerManagerActionType.ActionNotifications,
                    intentActionList = listOf(intent)
                )
            }

            /*MeizuSecurityCenterVersion.SecNM -> {
                intent.component = ComponentName(packageNotification, notificationActivityNew)
                KillerManagerAction(
                    KillerManagerActionType.ActionNotifications,
                    intentActionList = listOf(intent)
                )
            }*/

            else -> {
                KillerManagerAction(
                    KillerManagerActionType.ActionNotifications,
                    intentActionList = listOf(getDefaultSettingAction(context))
                )
            }
        }
    }

    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        val stringBuilder = StringBuilder(super.getExtraDebugInformations(packageManager))
        stringBuilder.append("MeizuSecVersionMethod:").append(getMeizuSecVersion(packageManager))

        val info: PackageInfo
        var versionStr = ""
        try {
            info = packageManager.getPackageInfo(packageDefault, 0)
            versionStr = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        stringBuilder.append("MeizuSecPackageVersion:").append(versionStr)

        // ----- PACKAGE INFORMATIONS -----
        stringBuilder
            .append(actionAppSpec)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionAppSpec
                )
            )

        stringBuilder
            .append(actionPowerSaving)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionPowerSaving
                )
            )

        stringBuilder
            .append(packageDefault + powerSavingActivityV22)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV22)
                )
            )

        stringBuilder
            .append(packageDefault + powerSavingActivityV34)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV34)
                )
            )

        stringBuilder
            .append(packageDefault + powerSavingActivityV37)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    ComponentName(packageDefault, powerSavingActivityV37)
                )
            )

        stringBuilder
            .append(packageDefault + notificationActivity)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionPowerSaving
                )
            )

        /*stringBuilder
            .append(packageNotification + notificationActivityNew)
            .append(
                ActionUtils.isIntentAvailable(
                    packageManager,
                    actionIntent = actionPowerSaving
                )
            )*/

        return stringBuilder.toString()
    }

    private enum class MeizuSecurityCenterVersion {
        Sec22, // Meizu security center : 2.2.0922, 2.2.0310
        Sec34, // Meizu security center : 3.4.0316
        Sec36, // Meizu security center : 3.6.0802
        Sec37, // Meizu security center : 3.7.1101
        Sec41, // Meizu security center : 4.1.10
        SecNM  // Meizu security center : N.M
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

                else ->
                    MeizuSecurityCenterVersion.Sec41
            }
        } catch (e: Exception) {
            MeizuSecurityCenterVersion.Sec41
        }

    companion object {
        // PACKAGE
        private const val packageDefault = "com.meizu.safe"
        //private const val packageNotification = "com.android.settings"

        // ACTION
        private const val actionAppSpec = "com.meizu.safe.security.SHOW_APPSEC"
        private const val actionPowerSaving = "com.meizu.power.PowerAppKilledNotification"

        // ACTIVITY
        private const val powerSavingActivityV22 = "com.meizu.safe.cleaner.RubbishCleanMainActivity"
        private const val powerSavingActivityV34 = "com.meizu.safe.powerui.AppPowerManagerActivity"
        private const val powerSavingActivityV37 =
            "com.meizu.safe.powerui.PowerAppPermissionActivity" // == ACTION com.meizu.power.PowerAppKilledNotification

        private const val notificationActivity = "com.meizu.safe.permission.NotificationActivity"
        //private const val notificationActivityNew = "com.android.settings.SubSettings"
    }
}
