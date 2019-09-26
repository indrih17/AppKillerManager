package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.Failure
import com.thelittlefireman.appkillermanager.InternalFail
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.utils.LogUtils

/**
 * Add users permission to app manifest:
 *
 * &lt;uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /&gt;
 */
open class ActionDialogCreator(
    private val device: DeviceBase,
    private val activity: AppCompatActivity,
    private val onFailure: (Failure) -> Unit
) {
    private val prefManager = PrefManager(activity)
    private lateinit var currentAction: KillerManagerActionType

    init {
        LogUtils.logCustomListener = { tag: String, message: String?, exception: Exception ->
            onFailure(InternalFail(tag, message, exception))
        }
    }

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
                    KillerManager
                        .doAction(activity, device, action)
                        .fold(
                            ifLeft = onFailure,
                            ifRight = {
                                currentAction = action
                            }
                        )
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
        KillerManager
            .onActivityResult(activity, device, action, requestCode)
            .fold(
                ifLeft = onFailure,
                ifRight = { actionHandled ->
                    if (actionHandled)
                        prefManager.setProgressStatus(
                            action,
                            ProgressOfEliminatingOptimizations.Completed
                        )
                }
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
}