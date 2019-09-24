package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer
import com.thelittlefireman.appkillermanager.utils.SystemUtils.emuiRomName

class Huawei : DeviceAbstract() {
    // TODO NOT SUR IT WORKS ON EMUI 5

    override val deviceManufacturer: Manufacturer = Manufacturer.Huawei

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            isEmotionUI ||
                    Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        ComponentName(
            packageSystemManager,
            "com.huawei.systemmanager.optimize.bootstart.BootStartActivity"
        ),
        ComponentName(
            packageSystemManager,
            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
        ),
        ComponentName(
            packageSystemManager,
            "com.huawei.permissionmanager.ui.MainActivity"
        )
    )

    override val intentActionList: List<String> = listOf(
        actionPowerSaving,
        actionAutoStart,
        actionNotification
    )

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean = false

    override fun isActionNotificationAvailable(): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(ActionUtils.createIntent(action = actionPowerSaving))
        )

    // AUTOSTART not used in huawei
    override fun getActionAutoStart(context: Context): KillerManagerAction? = null

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(action = actionNotification))
        )

    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        val result = super.getExtraDebugInformations(packageManager)
        val stringBuilder = StringBuilder(result)
            .append("ROM_VERSION: ")
            .append(emuiRomName)
            .append("\nHuaweiSystemManagerVersionMethod: ")
            .append(getHuaweiSystemManagerVersion(packageManager))

        try {
            stringBuilder
                .append("\nHuaweiSystemManagerPackageVersion: ")
                .append(
                    packageManager
                        .getPackageInfo(packageSystemManager, 0)
                        .versionName
                )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }

    companion object {
        // PACKAGE
        private const val packageSystemManager = "com.huawei.systemmanager"

        // ACTION
        private const val actionPowerSaving = "huawei.intent.action.HSM_PROTECTED_APPS"
        private const val actionAutoStart = "huawei.intent.action.HSM_BOOTAPP_MANAGER"
        private const val actionNotification = "huawei.intent.action.NOTIFICATIONMANAGER"

        @SuppressLint("DefaultLocale")
        private val isEmotionUI: Boolean =
            emuiRomName
                ?.let { romName ->
                    romName.toLowerCase().indexOf("emotionui_") == 0
                }
                ?: false

        private fun getHuaweiSystemManagerVersion(packageManager: PackageManager): Int {
            var version = 0
            var versionNum = 0
            var thirdPartFirstDigit = 0
            try {
                val info = packageManager.getPackageInfo(packageSystemManager, 0)
                Log.i(Huawei::class.java.name, "manager info = $info")
                val versionStr = info.versionName
                val versionTmp =
                    versionStr.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (versionTmp.size >= 2) {
                    versionNum = when {
                        Integer.parseInt(versionTmp[0]) == 5 ->
                            500

                        Integer.parseInt(versionTmp[0]) == 4 ->
                            Integer.parseInt(versionTmp[0] + versionTmp[1] + versionTmp[2])

                        else ->
                            Integer.parseInt(versionTmp[0] + versionTmp[1])
                    }
                }
                if (versionTmp.size >= 3)
                    thirdPartFirstDigit = Integer.valueOf(versionTmp[2].substring(0, 1))

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (versionNum >= 330)
                version = if (versionNum >= 500)
                    6
                else if (versionNum >= 400)
                    5
                else if (versionNum >= 331)
                    4
                else
                    if (thirdPartFirstDigit == 6 || thirdPartFirstDigit == 4 || thirdPartFirstDigit == 2)
                        3
                    else
                        2
            else if (versionNum != 0)
                version = 1

            return version
        }
    }
}