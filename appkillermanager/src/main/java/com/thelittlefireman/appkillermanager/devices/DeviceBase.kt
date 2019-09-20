package com.thelittlefireman.appkillermanager.devices

import android.content.ComponentName
import android.content.Context

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

    /**
     * @return true
     */
    fun isActionPowerSavingAvailable(context: Context): Boolean

    fun isActionAutoStartAvailable(context: Context): Boolean
    fun isActionNotificationAvailable(context: Context): Boolean
    fun needToUseAlongWithActionDoseMode(): Boolean

    fun getActionPowerSaving(context: Context): KillerManagerAction?

    fun getActionAutoStart(context: Context): KillerManagerAction?

    // FIXME IS IT REALY NEEDED ? ==> REPLACE BY OTHER FUNCTION ?
    fun getActionNotification(context: Context): KillerManagerAction?

    // TODO ADD FOR MEMORY OPTIMIZATION : https://github.com/00aj99/CRomAppWhitelist
    fun getExtraDebugInformations(context: Context): String

    /**
     * Function common in all devices
     * @param context the current context
     * @return the Intent to open the doze mode settings
     */
    fun getActionDozeMode(context: Context): KillerManagerAction?

    fun isActionDozeModeNotNecessary(context: Context): Boolean
}
