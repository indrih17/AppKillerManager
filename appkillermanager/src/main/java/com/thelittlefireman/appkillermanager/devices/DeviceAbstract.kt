package com.thelittlefireman.appkillermanager.devices

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.CallSuper
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils

abstract class DeviceAbstract : DeviceBase {
    @CallSuper
    override fun getExtraDebugInformations(packageManager: PackageManager): String {
        // ----- PACKAGE INFORMATIONS ----- //
        val resultBuilder = StringBuilder()
        componentNameList?.let { list ->
            for (componentName in list) {
                resultBuilder.append(componentName.packageName + componentName.className)
                resultBuilder.append(":")
                resultBuilder.append(ActionUtils.isIntentAvailable(packageManager, componentName))
                resultBuilder.append('\n')
            }
        }
        intentActionList?.let { list ->
            for (intentAction in list) {
                resultBuilder.append(intentAction)
                resultBuilder.append(":")
                resultBuilder.append(
                    ActionUtils.isIntentAvailable(
                        packageManager,
                        actionIntent = intentAction
                    )
                )
            }
        }
        return resultBuilder.toString()
    }

    override fun needToUseAlongWithActionDoseMode(): Boolean = false

    override fun isActionDozeModeNotNecessary(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(context.packageName)
        }
        return false
    }

    override fun getActionDozeMode(context: Context): KillerManagerAction? {
        //Android 7.0+ Doze
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val ignoringBatteryOptimizations =
                pm.isIgnoringBatteryOptimizations(context.packageName)

            if (!ignoringBatteryOptimizations) {
                val dozeIntent = ActionUtils.createIntent(
                    action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                )
                // Cannot fire Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                // due to Google play device policy restriction !
                return KillerManagerAction(
                    KillerManagerActionType.ActionPowerSaving,
                    intentActionList = listOf(dozeIntent)
                )
            } else {
                LogUtils.i(
                    javaClass.name,
                    "getActionDozeMode" + "App is already enable to ignore doze " +
                            "battery optimization"
                )
            }
        }
        return null
    }
}
