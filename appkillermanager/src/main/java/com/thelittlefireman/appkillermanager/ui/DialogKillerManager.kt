package com.thelittlefireman.appkillermanager.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.R
import com.thelittlefireman.appkillermanager.deviceUi.SettingFragment
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.KillerManagerUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import de.mrapp.android.dialog.WizardDialog

class DialogKillerManager constructor(
    private val activity: AppCompatActivity,
    @DrawableRes private val iconRes: Int = -1,
    private val titleMessage: String? = null,
    private val enableDontShowAgain: Boolean = false,
    private val contentHeaderMessage: String? = null,
    @StringRes private val titleResMessage1: Int = -1,
    @StringRes private val contentHeaderResMessage: Int = -1,
    private val killerManagerActionTypeList: List<KillerManagerActionType> = listOf(
        KillerManagerActionType.ActionEmpty
    )
) {
    fun show() {
        val device = KillerManager.device

        if (device == null) {
            LogUtils.i(this.javaClass.name, "Device not in the list no need to show the dialog")
            return
        }

        val dialogBuilder = WizardDialog.Builder(
            activity,
            R.style.MaterialDialog_Light_Fullscreen
        )
        val killerManagerActionList = KillerManager.getKillerManagerActionFromActionType(
            activity,
            killerManagerActionTypeList
        )

        if (killerManagerActionList.isEmpty()) {
            LogUtils.i(
                this.javaClass.name,
                "No action available for this device no need to show the dialog"
            )
            return
        }

        dialogBuilder.addFragment(
            SettingFragment::class.java,
            SettingFragment.generateArguments(killerManagerActionList, enableDontShowAgain)
        )
        dialogBuilder.showHeader(false)
        if (contentHeaderResMessage != -1)
            dialogBuilder.setMessage(contentHeaderResMessage)
        else
            dialogBuilder.setMessage(contentHeaderMessage)

        if (iconRes != -1)
            dialogBuilder.setIcon(this.iconRes)
        else
            dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)

        if (titleResMessage1 != -1)
            dialogBuilder.setTitle(titleResMessage1)
        else if (titleMessage != null && titleMessage.isNotEmpty())
            dialogBuilder.setTitle(titleMessage)
        else
            dialogBuilder.setTitle(
                activity.getString(
                    R.string.dialog_title_notification,
                    device.deviceManufacturer.toString()
                )
            )

        if (!enableDontShowAgain || !KillerManagerUtils.isDontShowAgain(activity)) {
            dialogBuilder
                .create()
                .show(activity.supportFragmentManager, dialogTagKey)
        }
    }

    companion object {
        private const val dialogTagKey = "KILLER_MANAGER_DIALOG"
    }
}