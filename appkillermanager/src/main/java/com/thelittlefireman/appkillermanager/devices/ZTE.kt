package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class ZTE : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Zte

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    override val componentNameList: List<ComponentName> = listOf(
        zteComponentNamesAutoStart,
        zteComponentNamesPowerSave
    )

    override val intentActionList: List<String> = emptyList()

    override fun isActionPowerSavingAvailable(context: Context): Boolean = true

    override fun isActionAutoStartAvailable(): Boolean = true

    override fun isActionNotificationAvailable(): Boolean = false

    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionPowerSaving,
            intentActionList = listOf(ActionUtils.createIntent(zteComponentNamesPowerSave))
        )

    override fun getActionAutoStart(context: Context): KillerManagerAction? =
        KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            intentActionList = listOf(ActionUtils.createIntent(zteComponentNamesAutoStart))
        )

    override fun getActionNotification(context: Context): KillerManagerAction? = null

    companion object {
        private const val ztePackageName = "com.zte.heartyservice"

        private val zteComponentNamesAutoStart = ComponentName(
            ztePackageName,
            "com.zte.heartyservice.autorun.AppAutoRunManager"
        )

        private val zteComponentNamesPowerSave = ComponentName(
            ztePackageName,
            "com.zte.heartyservice.setting.ClearAppSettingsActivity"
        )
    }
}
