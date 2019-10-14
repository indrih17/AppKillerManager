package com.thelittlefireman.appkillermanager.managers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.thelittlefireman.appkillermanager.Either
import com.thelittlefireman.appkillermanager.IntentListForActionNotFoundFail
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
        availableIntents: List<Intent>,
        actionType: KillerManagerActionType
    ) {
        val intent = getIntentForActionOrNull(availableIntents, actionType)
            ?: return // Finished checking

        activity.startActivityForResult(intent, requestCodeKillerManagerAction)
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
    fun getAvailableIntentsFromActionType(
        context: Context,
        device: DeviceBase,
        actionType: KillerManagerActionType
    ): Either<IntentListForActionNotFoundFail, List<Intent>> {
        val intentList = context.getIntentListForAction(device, actionType)
        val availableIntents =
            ActionUtils.filterAvailableIntents(context.packageManager, intentList)
        return if (availableIntents.isNotEmpty())
        // Intent found actionType succeed
            Either.right(availableIntents)
        else
        // Intent not found actionType failed
            Either.left(
                IntentListForActionNotFoundFail(
                    actionType = actionType,
                    packageManager = context.packageManager,
                    device = device,
                    extraDebugInfo = ActionUtils.getExtrasDebugInformation(intentList)
                )
            )
    }

    private fun Context.getIntentListForAction(
        device: DeviceBase,
        actionType: KillerManagerActionType
    ): List<Intent> =
        when (actionType) {
            ActionAutoStart ->
                device.getActionAutoStart(this)?.intentActionList ?: emptyList()

            ActionPowerSaving ->
                device.getActionPowerSaving(this)?.intentActionList ?: emptyList()

            ActionNotifications ->
                device.getActionNotification(this)?.intentActionList ?: emptyList()
        }

    fun onActivityResult(
        activity: FragmentActivity,
        intentList: List<Intent>,
        actionType: KillerManagerActionType,
        requestCode: Int
    ): Boolean =
        if (requestCode == requestCodeKillerManagerAction) {
            val old = currentNbActions.getValue(actionType)
            currentNbActions[actionType] = old + 1

            doAction(activity, intentList, actionType)
            true
        } else {
            false
        }

    private const val requestCodeKillerManagerAction = 52000
}
