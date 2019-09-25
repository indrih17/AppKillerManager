package com.thelittlefireman.appkillermanager.managers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType.*
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import java.util.*
import kotlin.collections.set

object KillerManager {
    val device: DeviceBase? = DevicesManager.getDevice()

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
        actionType: KillerManagerActionType
    ): Boolean =
        activity.doAction(
            actionType,
            currentNbActions.getValue(actionType)
        )

    /**
     * Execute the actionType
     *
     * @param actionType the wanted actionType to execute
     * @return true : [actionType] succeed; false [actionType] failed
     */
    // Avoid main app to crash when intent denied by using try catch
    private fun FragmentActivity.doAction(
        actionType: KillerManagerActionType,
        index: Int
    ): Boolean {
        try {
            val intentList = getIntentFromActionType(actionType)
            if (intentList.isNotEmpty() && intentList.size > index) {
                val intent = intentList[index]
                if (ActionUtils.isIntentAvailable(packageManager, intent)) {
                    startActivityForResult(intent, requestCodeKillerManagerAction)
                    // Intent found actionType succeed
                    return true
                }
            }
        } catch (e: Exception) {
            // Exception handle actionType failed
            LogUtils.e(KillerManager::class.java.name, e)
        }
        return false
    }

    /**
     * Return the intent for a specific actionType
     *
     * @param actionType the wanted actionType
     * @return the intent
     */
    private fun Context.getIntentFromActionType(
        actionType: KillerManagerActionType
    ): List<Intent> {
        if (device != null) {
            val intentList: List<Intent> = when (actionType) {
                ActionAutoStart ->
                    device.getActionAutoStart(this)?.intentActionList ?: emptyList()

                ActionPowerSaving ->
                    device.getActionPowerSaving(this)?.intentActionList ?: emptyList()

                ActionNotifications ->
                    device.getActionNotification(this)?.intentActionList ?: emptyList()

                ActionEmpty ->
                    emptyList()
            }
            return if (intentList.isNotEmpty()
                && ActionUtils.isAtLeastOneIntentAvailable(packageManager, intentList)
            ) {
                // Intent found actionType succeed
                intentList
            } else {
                LogUtils.intentNotFound(
                    packageManager = packageManager,
                    device = device,
                    extraDebugInfo = ActionUtils.getExtrasDebugInformations(intentList),
                    actionType = actionType
                )
                // Intent not found actionType failed
                emptyList()
            }
        } else {
            // device not found actionType failed
            return emptyList()
        }
    }

    fun onActivityResult(
        activity: FragmentActivity,
        actionType: KillerManagerActionType,
        requestCode: Int
    ): Boolean {
        if (requestCode == requestCodeKillerManagerAction) {
            currentNbActions[actionType]?.let {
                currentNbActions[actionType] = it + 1
            }

            val intentList = activity.getIntentFromActionType(actionType)
            val actionIndex = currentNbActions[actionType]
            if (intentList.isNotEmpty() && actionIndex != null && intentList.size > actionIndex)
                activity.doAction(actionType, actionIndex)
            else
            // reset if no more intent
                currentNbActions[actionType] = 0
            return true
        } else {
            return false
        }
    }

    private const val requestCodeKillerManagerAction = 52000
}
