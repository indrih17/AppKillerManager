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
        Lenovo(),
        Xiaomi(),
        ZTE()
    )

    fun getDevice(): DeviceBase? =
        deviceBaseList
            .firstOrNull { it.isThatRom }
            ?.also {
                LogUtils.w(
                    DevicesManager::class.java.name,
                    "Unsupported device"
                )

                LogUtils.e(
                    DevicesManager::class.java.name,
                    SystemUtils.defaultDebugInformation
                )
            }
}
