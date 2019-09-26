package com.thelittlefireman.appkillermanager

import android.content.Intent
import android.content.pm.PackageManager
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.SystemUtils

sealed class Failure

class InternalFail(
    val tag: String,
    val message: String?,
    val exception: Exception
) : Failure()

class UnknownDeviceFail(val debugInformation: String) : Failure()

sealed class IntentFailure : Failure()

class IntentNotAvailableFail(val intent: Intent) : IntentFailure()

class IntentListForActionNotFoundFail(
    actionType: KillerManagerActionType,
    packageManager: PackageManager,
    device: DeviceBase,
    extraDebugInfo: String
) : IntentFailure() {
    val debugInformation =
        """
            INTENT NOT FOUND: $extraDebugInfo,
            Library version name: ${BuildConfig.VERSION_NAME},
            Library version code: ${BuildConfig.VERSION_CODE},
            KillerManagerActionType: ${actionType.name},
            System utils: ${SystemUtils.defaultDebugInformation},
            Device: ${device.getExtraDebugInformations(packageManager)}
        """.trimIndent()
}
