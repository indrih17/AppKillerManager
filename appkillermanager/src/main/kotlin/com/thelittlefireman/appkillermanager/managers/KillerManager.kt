package com.thelittlefireman.appkillermanager.managers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.core.Either
import arrow.core.flatMap
import com.thelittlefireman.appkillermanager.IntentFailure
import com.thelittlefireman.appkillermanager.IntentListForActionNotFoundFail
import com.thelittlefireman.appkillermanager.IntentNotAvailableFail
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType.*
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import java.util.*
import kotlin.collections.set

object KillerManager {
    private val currentNbActions: HashMap<KillerManagerActionType, Int> = hashMapOf(
        ActionAutoStart to 0,
        ActionNotifications to 0,
        ActionPowerSaving to 0
    )

    /**
     * Execute the actionType
     *
     * @param activity                the current activity
     * @param actionType the wanted actionType to execute
     * @return true : actionType succeed; false actionType failed
     */
    fun doAction(
        activity: FragmentActivity,
        device: DeviceBase,
        actionType: KillerManagerActionType
    ): Either<IntentFailure, Unit> =
        activity
            .getIntentListFromActionType(device, actionType)
            .flatMap { intentList ->
                val intent = getIntentForActionOrNull(intentList, actionType)
                    ?: return@flatMap Either.right(Unit) // Finished checking

                return@flatMap if (ActionUtils.isIntentAvailable(activity.packageManager, intent)) {
                    activity.startActivityForResult(intent, requestCodeKillerManagerAction)
                    // Intent found actionType succeed
                    Either.right(Unit)
                } else {
                    Either.left(IntentNotAvailableFail(intent))
                }
            }

    private fun getIntentForActionOrNull(
        intentList: List<Intent>,
        actionType: KillerManagerActionType
    ): Intent? {
        val actionIndex = currentNbActions.getValue(actionType)
        return intentList
            .getOrNull(actionIndex)
            .also {
                if (it == null) currentNbActions[actionType] = 0
            }
    }

    /**
     * Return the intent for a specific actionType
     *
     * @param actionType the wanted actionType
     * @return the intent list of failure.
     */
    private fun Context.getIntentListFromActionType(
        device: DeviceBase,
        actionType: KillerManagerActionType
    ): Either<IntentFailure, List<Intent>> {
        val intentList: List<Intent> = when (actionType) {
            ActionAutoStart ->
                device.getActionAutoStart(this)?.intentActionList ?: emptyList()

            ActionPowerSaving ->
                device.getActionPowerSaving(this)?.intentActionList ?: emptyList()

            ActionNotifications ->
                device.getActionNotification(this)?.intentActionList ?: emptyList()
        }
        return if (intentList.isNotEmpty()
            && ActionUtils.isAtLeastOneIntentAvailable(packageManager, intentList)
        )
        // Intent found actionType succeed
            Either.right(intentList)
        else
        // Intent not found actionType failed
            Either.left(
                IntentListForActionNotFoundFail(
                    actionType = actionType,
                    packageManager = packageManager,
                    device = device,
                    extraDebugInfo = ActionUtils.getExtrasDebugInformation(intentList)
                )
            )
    }

    fun onActivityResult(
        activity: FragmentActivity,
        device: DeviceBase,
        actionType: KillerManagerActionType,
        requestCode: Int
    ): Either<IntentFailure, Boolean> =
        if (requestCode == requestCodeKillerManagerAction) {
            val old = currentNbActions.getValue(actionType)
            currentNbActions[actionType] = old + 1
            doAction(activity, device, actionType).map { true }
        } else {
            Either.right(false)
        }

    private const val requestCodeKillerManagerAction = 52000
}
