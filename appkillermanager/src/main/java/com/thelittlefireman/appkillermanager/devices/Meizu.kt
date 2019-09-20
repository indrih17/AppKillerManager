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

    //com.meizu.safe.SecurityCenterActivity
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

    override fun isActionAutoStartAvailable(context: Context): Boolean = true

    override fun isActionNotificationAvailable(context: Context): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? {
        var intent = ActionUtils.createIntent(
            action = MeizuPowerSavingAction
        )
        val mSecVersion = getMeizuSecVersion(context)
        if (ActionUtils.isIntentAvailable(context, intent)) {
            return KillerManagerAction(
                KillerManagerActionType.ActionPowerSaving,
                intentActionList = listOf(intent)
            )
        }
        intent = ActionUtils.createIntent()
        when (mSecVersion) {
            MeizuSecurityCenterVersion.Sec22 ->
                intent.setClassName(MeizuDefaultPackage, MeizuPowerSavingActivityV22)

            MeizuSecurityCenterVersion.Sec34 ->
                intent.setClassName(MeizuDefaultPackage, MeizuPowerSavingActivityV34)

            MeizuSecurityCenterVersion.Sec37 ->
                intent.setClassName(MeizuDefaultPackage, MeizuPowerSavingActivityV37)

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
        val intent = ActionUtils.createIntent(action = MeizuDefaultActionAppSpec)
        intent.putExtra(MeizuDefaultExtraPackage, context.packageName)
        return intent
    }

    override fun getActionNotification(context: Context): KillerManagerAction? {
        val mSecVersion = getMeizuSecVersion(context)
        val intent = ActionUtils.createIntent()
        return if (mSecVersion == MeizuSecurityCenterVersion.Sec37 || mSecVersion == MeizuSecurityCenterVersion.Sec41) {
            intent.component = ComponentName(MeizuDefaultPackage, MeizuNotificationActivity)
            KillerManagerAction(
                KillerManagerActionType.ActionNotifications,
                intentActionList = listOf(intent)
            )
        } else {
            KillerManagerAction(
                KillerManagerActionType.ActionNotifications,
                intentActionList = listOf(getDefaultSettingAction(context))
            )
        }
    }

    override fun getExtraDebugInformations(context: Context): String {
        val stringBuilder = StringBuilder(super.getExtraDebugInformations(context))
        stringBuilder.append("MeizuSecVersionMethod:").append(getMeizuSecVersion(context))

        val manager = context.packageManager
        val info: PackageInfo
        var versionStr = ""
        try {
            info = manager.getPackageInfo(MeizuDefaultPackage, 0)
            versionStr = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        stringBuilder.append("MeizuSecPackageVersion:").append(versionStr)

        // ----- PACKAGE INFORMATIONS -----
        stringBuilder
            .append(MeizuDefaultActionAppSpec)
            .append(
                ActionUtils.isIntentAvailable(
                    context,
                    actionIntent = MeizuDefaultActionAppSpec
                )
            )

        stringBuilder
            .append(MeizuPowerSavingAction)
            .append(ActionUtils.isIntentAvailable(context, actionIntent = MeizuPowerSavingAction))

        stringBuilder
            .append(MeizuDefaultPackage + MeizuPowerSavingActivityV22)
            .append(
                ActionUtils.isIntentAvailable(
                    context,
                    ComponentName(MeizuDefaultPackage, MeizuPowerSavingActivityV22)
                )
            )

        stringBuilder
            .append(MeizuDefaultPackage + MeizuPowerSavingActivityV34)
            .append(
                ActionUtils.isIntentAvailable(
                    context,
                    ComponentName(MeizuDefaultPackage, MeizuPowerSavingActivityV34)
                )
            )

        stringBuilder
            .append(MeizuDefaultPackage + MeizuPowerSavingActivityV37)
            .append(
                ActionUtils.isIntentAvailable(
                    context,
                    ComponentName(MeizuDefaultPackage, MeizuPowerSavingActivityV37)
                )
            )

        stringBuilder
            .append(MeizuDefaultPackage + MeizuNotificationActivity)
            .append(ActionUtils.isIntentAvailable(context, actionIntent = MeizuPowerSavingAction))

        return stringBuilder.toString()
    }

    private enum class MeizuSecurityCenterVersion {
        Sec22, //Meizu security center : 2.2.0922, 2.2.0310
        Sec34, //Meizu security center : 3.4.0316
        Sec36, //Meizu security center : 3.6.0802
        Sec37, //Meizu security center : 3.7.1101
        Sec41  //Meizu security center : 4.1.10
    }

    private fun getMeizuSecVersion(context: Context): MeizuSecurityCenterVersion {
        var v: MeizuSecurityCenterVersion
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(MeizuDefaultPackage, 0)
            val versionStr = info.versionName //2.2.0922;
            Log.i("Meizu security center :", versionStr)
            v = when {
                versionStr.startsWith("2") ->
                    MeizuSecurityCenterVersion.Sec22

                versionStr.startsWith("3") -> {
                    val d = Integer.parseInt(versionStr.substring(2, 3))
                    Log.i("Meizu security center :", "d: $d")
                    when {
                        d <= 4 -> MeizuSecurityCenterVersion.Sec34
                        d < 7 -> MeizuSecurityCenterVersion.Sec36
                        else -> MeizuSecurityCenterVersion.Sec37
                    }
                }

                versionStr.startsWith("4") ->
                    MeizuSecurityCenterVersion.Sec41

                else ->
                    MeizuSecurityCenterVersion.Sec41
            }
        } catch (e: Exception) {
            v = MeizuSecurityCenterVersion.Sec41
        }

        return v
    }

    companion object {
        private const val MeizuDefaultActionAppSpec = "com.meizu.safe.security.SHOW_APPSEC"
        private const val MeizuPowerSavingAction = "com.meizu.power.PowerAppKilledNotification"
        private const val MeizuDefaultExtraPackage = "packageName"
        private const val MeizuDefaultPackage = "com.meizu.safe"
        private const val MeizuPowerSavingActivityV22 =
            "com.meizu.safe.cleaner.RubbishCleanMainActivity"
        private const val MeizuPowerSavingActivityV34 =
            "com.meizu.safe.powerui.AppPowerManagerActivity"
        private const val MeizuPowerSavingActivityV37 =
            "com.meizu.safe.powerui.PowerAppPermissionActivity" // == ACTION com.meizu.power.PowerAppKilledNotification
        private const val MeizuNotificationActivity =
            "com.meizu.safe.permission.NotificationActivity"
    }
}
