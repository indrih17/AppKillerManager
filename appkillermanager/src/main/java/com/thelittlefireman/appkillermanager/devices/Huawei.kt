package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.thelittlefireman.appkillermanager.R
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

    private val helpImagePowerSaving: Int = R.drawable.huawei_powersaving

    override val componentNameList: List<ComponentName> = listOf(
        ComponentName(
            huaweiSystemManagerPackageName,
            "com.huawei.systemmanager.optimize.bootstart.BootStartActivity"
        ),
        ComponentName(
            huaweiSystemManagerPackageName,
            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
        ),
        ComponentName(
            huaweiSystemManagerPackageName,
            "com.huawei.permissionmanager.ui.MainActivity"
        )
    )

    override val intentActionList: List<String> = listOf(
        huaweiActionPowerSaving,
        huaweiActionAutoStart,
        huaweiActionNotification
    )

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(context: Context): Boolean = false

    override fun isActionNotificationAvailable(context: Context): Boolean = true

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            helpImages = listOf(helpImagePowerSaving),
            intentActionList = listOf(ActionUtils.createIntent(action = huaweiActionPowerSaving))
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? {
        // AUTOSTART not used in huawei
        return KillerManagerAction()
        /*Intent intent = ActionUtils.createIntent();
        intent.setAction(huaweiActionAutoStart);
        if (ActionUtils.isIntentAvailable(context, intent)) {
            return intent;
        } else {
            intent = ActionUtils.createIntent();
            intent.setComponent(getComponentNameAutoStart(context));
            return intent;
        }*/
    }

    override fun getActionNotification(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            intentActionList = listOf(ActionUtils.createIntent(action = huaweiActionNotification))
        )

    override fun getExtraDebugInformations(context: Context): String {
        val result = super.getExtraDebugInformations(context)
        val stringBuilder = StringBuilder(result)
        stringBuilder.append("ROM_VERSION").append(emuiRomName)
        stringBuilder.append("HuaweiSystemManagerVersionMethod:")
            .append(getHuaweiSystemManagerVersion(context))
        val manager = context.packageManager
        val info: PackageInfo
        var versionStr = ""
        try {
            info = manager.getPackageInfo(huaweiSystemManagerPackageName, 0)
            versionStr = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        stringBuilder.append("HuaweiSystemManagerPackageVersion:").append(versionStr)
        return stringBuilder.toString()
    }

    private fun getComponentNameAutoStart(context: Context): ComponentName {
        val mVersion = getHuaweiSystemManagerVersion(context)
        return if (mVersion == 4 || mVersion == 5)
            componentNameList[1]
        else if (mVersion == 6)
            componentNameList[2]
        else
            componentNameList[0]
    }

    companion object {
        private const val huaweiSystemManagerPackageName = "com.huawei.systemmanager"
        private const val huaweiActionPowerSaving = "huawei.intent.action.HSM_PROTECTED_APPS"
        private const val huaweiActionAutoStart = "huawei.intent.action.HSM_BOOTAPP_MANAGER"
        private const val huaweiActionNotification = "huawei.intent.action.NOTIFICATIONMANAGER"

        private val isEmotionUI: Boolean
            @SuppressLint("DefaultLocale")
            get() {
                val romName = emuiRomName
                return if (romName != null)
                    romName.toLowerCase().indexOf("emotionui_") == 0
                else
                    false
            }

        private fun getHuaweiSystemManagerVersion(context: Context): Int {
            var version = 0
            var versionNum = 0
            var thirdPartFirtDigit = 0
            try {
                val manager = context.packageManager
                val info = manager.getPackageInfo(huaweiSystemManagerPackageName, 0)
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
                if (versionTmp.size >= 3) {
                    thirdPartFirtDigit = Integer.valueOf(versionTmp[2].substring(0, 1))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (versionNum >= 330) {
                version = if (versionNum >= 500) {
                    6
                } else if (versionNum >= 400) {
                    5
                } else if (versionNum >= 331) {
                    4
                } else {
                    if (thirdPartFirtDigit == 6 || thirdPartFirtDigit == 4 || thirdPartFirtDigit == 2) 3 else 2
                }
            } else if (versionNum != 0) {
                version = 1
            }
            return version
        }
    }
}
