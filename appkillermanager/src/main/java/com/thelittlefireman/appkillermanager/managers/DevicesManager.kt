package com.thelittlefireman.appkillermanager.managers

import com.thelittlefireman.appkillermanager.devices.*
import com.thelittlefireman.appkillermanager.utils.LogUtils
import com.thelittlefireman.appkillermanager.utils.SystemUtils

object DevicesManager {
    private val deviceBaseList = listOf(
        Asus(),
        Huawei(),
        Letv(),
        Meizu(),
        OnePlus(),
        Oppo(),
        HTC(),
        Samsung(),
        Vivo(),
        Xiaomi(),
        ZTE()
    )

    fun getDevice(): DeviceBase? =
        deviceBaseList
            .filter { it.isThatRom }
            .let { deviceList ->
                deviceList.singleOrNull() ?: run {
                    LogUtils.e(
                        DevicesManager::class.java.name,
                        if (deviceList.isEmpty())
                            "Unsupported device"
                        else
                            "MORE THAN ONE CORRESPONDING: ${
                            deviceList.joinToString { it.deviceManufacturer.toString() }
                            }"
                    )
                    LogUtils.e(
                        DevicesManager::class.java.name,
                        SystemUtils.defaultDebugInformation
                    )
                    null
                }
            }
}
