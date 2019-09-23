package com.thelittlefireman.appkillermanager.devices

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.utils.Manufacturer

interface DeviceBase {
    val deviceManufacturer: Manufacturer

    /**
     * It’s true if the manufacturer of the device on which
     * this application is running matches the [deviceManufacturer].
     */
    val isThatRom: Boolean

    val componentNameList: List<ComponentName>?
    val intentActionList: List<String>?

    fun isActionPowerSavingAvailable(context: Context): Boolean
    fun isActionAutoStartAvailable(packageManager: PackageManager): Boolean
    fun isActionNotificationAvailable(): Boolean

    fun needToUseAlongWithActionDoseMode(): Boolean

    fun getActionPowerSaving(context: Context): KillerManagerAction?
    fun getActionAutoStart(context: Context): KillerManagerAction?
    fun getActionNotification(context: Context): KillerManagerAction?

    // TODO ADD FOR MEMORY OPTIMIZATION : https://github.com/00aj99/CRomAppWhitelist
    fun getExtraDebugInformations(packageManager: PackageManager): String

    /**
     * Function common in all devices
     * @param context the current context
     * @return the Intent to open the doze mode settings
     */
    fun getActionDozeMode(context: Context): KillerManagerAction?

    fun isActionDozeModeNotNecessary(context: Context): Boolean
}
