package com.thelittlefireman.appkillermanager.managers

import com.thelittlefireman.appkillermanager.devices.*
import com.thelittlefireman.appkillermanager.exceptions.UnknownDeviceException
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
        Lenovo(),
        Xiaomi(),
        ZTE()
    )

    fun getDevice(): DeviceBase? =
        deviceBaseList
            .firstOrNull { it.isThatRom }
            .also {
                if (it == null)
                    LogUtils.e(
                        DevicesManager::class.java.name,
                        exception = UnknownDeviceException(
                            "Unsupported device: \n${SystemUtils.defaultDebugInformation}"
                        )
                    )
            }
}
