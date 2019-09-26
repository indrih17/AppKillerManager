package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType

/**
 * Add users permission to app manifest:
 *
 * &lt;uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /&gt;
 */
open class ActionDialogCreator(
    private val device: DeviceBase,
    private val activity: AppCompatActivity
) {
    private val prefManager = PrefManager(activity)
    private lateinit var currentAction: KillerManagerActionType

    fun showDialogForAction(action: KillerManagerActionType, messageRes: Int) {
        if (
            action.isAvailable(activity)
            && prefManager.getProgressStatus(action).needToShow()
        )
            activity.showDialog(
                messageRes,
                okButton = {
                    prefManager.setProgressStatus(
                        action,
                        ProgressOfEliminatingOptimizations.UserAgreed
                    )
                    val isSuccess = KillerManager.doAction(activity, action)
                    if (isSuccess)
                        currentAction = action
                },
                noButton = {
                    prefManager.setProgressStatus(
                        action,
                        ProgressOfEliminatingOptimizations.UserDenied
                    )
                }
            )
    }

    private fun KillerManagerActionType.isAvailable(context: Context) =
        when (this) {
            KillerManagerActionType.ActionAutoStart ->
                device.isActionAutoStartAvailable(context.packageManager)

            KillerManagerActionType.ActionPowerSaving ->
                device.isActionPowerSavingAvailable(context)

            KillerManagerActionType.ActionNotifications ->
                device.isActionNotificationAvailable()
        }

    fun onActivityResult(requestCode: Int) {
        val action = currentAction
        val isSuccess = KillerManager.onActivityResult(activity, action, requestCode)
        prefManager.setProgressStatus(
            action,
            if (isSuccess)
                ProgressOfEliminatingOptimizations.Completed
            else
                ProgressOfEliminatingOptimizations.UserAgreed
        )
    }

    protected open fun Context.showDialog(
        messageForUser: Int,
        okButton: () -> Unit,
        noButton: () -> Unit
    ): Unit =
        alert(
            message = messageForUser,
            positiveButtonText = android.R.string.yes,
            onOkButtonClick = okButton,
            onNoButtonClick = noButton,
            windowFeature = Window.FEATURE_NO_TITLE,
            cancelable = false
        )

    private fun ProgressOfEliminatingOptimizations.needToShow(): Boolean =
        this == ProgressOfEliminatingOptimizations.NotStarted
                || this == ProgressOfEliminatingOptimizations.UserAgreed
}