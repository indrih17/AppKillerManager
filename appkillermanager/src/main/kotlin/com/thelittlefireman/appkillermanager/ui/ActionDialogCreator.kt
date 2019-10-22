package com.thelittlefireman.appkillermanager.ui

import android.content.Context
import android.content.Intent
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.Failure
import com.thelittlefireman.appkillermanager.IntentNotAvailableFail
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
    private var intentList: List<Intent>? = null
    private var currentAction: KillerManagerActionType? = null

    init {
        LogUtils.logCustomListener = object : LogUtils.LogListener {
            override fun e(tag: String, message: String?, exception: Exception) =
                onFailure(InternalFail(tag, message, exception))

            override fun intentNotAvailable(intent: Intent) =
                onFailure(IntentNotAvailableFail(intent))
        }
    }

    fun showDialogForAction(actionType: KillerManagerActionType, messageRes: Int) {
        try {
            if (actionType.isAvailable(activity))
                KillerManager
                    .getAvailableIntentsFromActionType(activity, device, actionType)
                    .fold(
                        ifLeft = onFailure,
                        ifRight = { availableIntents ->
                            val intentsToShow = prefManager
                                .getProgressStatusList(availableIntents)
                                .filter { (_, status) -> status.needToShow() }
                                .map { (intent, _) -> intent }

                            if (intentsToShow.isNotEmpty())
                                activity.showDialog(
                                    messageForUser = messageRes,
                                    okButton = { actionAgreed(actionType, intentsToShow) },
                                    noButton = { actionDenied(intentsToShow) }
                                )
                        }
                    )
        } catch (e: Exception) {
            onFailure(InternalFail("ActionDialogCreator", "", e))
        }
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

    private fun actionAgreed(actionType: KillerManagerActionType, intentsToShow: List<Intent>) {
        prefManager.setProgressStatusList(
            intentsToShow,
            ProgressOfEliminatingOptimizations.UserAgreed
        )
        intentList = intentsToShow
        currentAction = actionType
        KillerManager.doAction(activity, intentsToShow, actionType)
    }

    private fun actionDenied(availableIntents: List<Intent>) =
        prefManager.setProgressStatusList(
            availableIntents,
            ProgressOfEliminatingOptimizations.UserDenied
        )

    fun onActivityResult(requestCode: Int) {
        try {
            val intents = intentList ?: return
            val action = currentAction ?: return
            val actionHandled =
                KillerManager.onActivityResult(activity, intents, action, requestCode)
            if (actionHandled)
                prefManager.setProgressStatusList(
                    intents,
                    ProgressOfEliminatingOptimizations.Completed
                )
        } catch (e: Exception) {
            onFailure(InternalFail("ActionDialogCreator", "", e))
        }
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