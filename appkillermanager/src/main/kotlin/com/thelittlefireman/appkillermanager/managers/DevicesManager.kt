package com.thelittlefireman.appkillermanager.managers

import com.thelittlefireman.appkillermanager.Either
import com.thelittlefireman.appkillermanager.UnknownDeviceFail
import com.thelittlefireman.appkillermanager.devices.*
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

    fun getDevice(): Either<UnknownDeviceFail, DeviceBase> =
        deviceBaseList
            .firstOrNull { it.isThatRom }
            .let {
                if (it != null)
                    Either.right(it)
                else
                    Either.left(UnknownDeviceFail(SystemUtils.defaultDebugInformation))
            }
}
