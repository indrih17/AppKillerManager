package com.thelittlefireman.appkillermanager.devices

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import com.thelittlefireman.appkillermanager.R
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.Manufacturer

class Asus : DeviceAbstract() {
    override val deviceManufacturer: Manufacturer = Manufacturer.Asus

    @SuppressLint("DefaultLocale")
    override val isThatRom: Boolean =
        deviceManufacturer.toString().let { manufacturer ->
            Build.BRAND.equals(manufacturer, ignoreCase = true) ||
                    Build.MANUFACTURER.equals(manufacturer, ignoreCase = true) ||
                    Build.FINGERPRINT.toLowerCase().contains(manufacturer)
        }

    @DrawableRes
    private val helpImageAutoStart: Int = R.drawable.asus_autostart

    @DrawableRes
    private val helpImageNotification: Int = R.drawable.asus_notification

    override val componentNameList: List<ComponentName> = listOf(
        asusComponentNameAutoStart,
        asusComponentNameNotification
    )

    override val intentActionList: List<String>? = null

    override fun isActionPowerSavingAvailable(context: Context): Boolean =
        super.isActionDozeModeNotNecessary(context)

    override fun isActionAutoStartAvailable(context: Context): Boolean = true

    override fun isActionNotificationAvailable(context: Context): Boolean = true

    // Juste need to use the regular battery non optimization permission =)
    override fun getActionPowerSaving(context: Context): KillerManagerAction? =
        super.getActionDozeMode(context)

    override fun getActionAutoStart(context: Context): KillerManagerAction? {
        val intent = ActionUtils.createIntent(asusComponentNameAutoStart)
        intent.putExtra("showNotice", true)
        return KillerManagerAction(
            KillerManagerActionType.ActionAutoStart,
            helpImages = listOf(helpImageAutoStart),
            intentActionList = listOf(intent)
        )
    }

    override fun getActionNotification(context: Context): KillerManagerAction? {
        // Need to click on notifications items
        val intent = ActionUtils.createIntent(asusComponentNameNotification)
        intent.putExtra("showNotice", true)
        return KillerManagerAction(
            KillerManagerActionType.ActionNotifications,
            helpImages = listOf(helpImageNotification),
            intentActionList = listOf(intent)
        )
    }

    companion object {
        private const val asusPackageMobileManager = "com.asus.mobilemanager"

        private val asusComponentNameNotification = ComponentName(
            asusPackageMobileManager,
            "com.asus.mobilemanager.entry.FunctionActivity"
        )

        private val asusComponentNameAutoStart = ComponentName(
            asusPackageMobileManager,
            "com.asus.mobilemanager.autostart.AutoStartActivity"
        )
    }
}
