package com.thelittlefireman.appkillermanager.managers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType.*
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.collections.getValue
import kotlin.collections.hashMapOf
import kotlin.collections.isNotEmpty
import kotlin.collections.set

object KillerManager {
    var device: DeviceBase? = DevicesManager.getDevice()
        private set

    private val currentNbActions: HashMap<KillerManagerActionType, Int> = hashMapOf(
        ActionAutoStart to 0,
        ActionNotifications to 0,
        ActionPowerSaving to 0
    )

    /**
     * Return the intent for a specific killerManagerActionType
     *
     * @param context                     the current context
     * @param killerManagerActionTypeList the wanted killerManagerActionType
     * @return
     */
    fun getKillerManagerActionFromActionType(
        context: Context,
        killerManagerActionTypeList: List<KillerManagerActionType>
    ): List<KillerManagerAction> {
        val packageManager = context.packageManager

        val device = DevicesManager.getDevice()
        this.device = device
        if (device != null) {
            val killerManagerActionList = ArrayList<KillerManagerAction>()
            for (actionType in killerManagerActionTypeList) {
                when (actionType) {
                    ActionAutoStart -> {
                        val actionAutoStart: KillerManagerAction? =
                            device.getActionAutoStart(context)
                        if (device.isActionAutoStartAvailable()
                            && actionAutoStart != null
                            && ActionUtils.isAtLeastOneIntentAvailable(
                                packageManager,
                                actionAutoStart
                            )
                        )
                            killerManagerActionList.add(actionAutoStart)
                    }

                    ActionPowerSaving -> {
                        val actionPowerSaving: KillerManagerAction? =
                            device.getActionPowerSaving(context)
                        if (device.isActionPowerSavingAvailable(context)
                            && actionPowerSaving != null
                            && ActionUtils.isAtLeastOneIntentAvailable(
                                packageManager,
                                actionPowerSaving
                            )
                        )
                            killerManagerActionList.add(actionPowerSaving)
                    }

                    ActionNotifications -> {
                        val actionNotification: KillerManagerAction? =
                            device.getActionNotification(context)
                        if (device.isActionNotificationAvailable()
                            && actionNotification != null
                            && ActionUtils.isAtLeastOneIntentAvailable(
                                packageManager,
                                actionNotification
                            )
                        )
                            killerManagerActionList.add(actionNotification)
                    }

                    ActionEmpty -> Unit
                }

                // do nothing
                if (killerManagerActionList.isEmpty())
                    LogUtils.indentNotFound(
                        packageManager = packageManager,
                        device = device,
                        extraDebugInfo = ActionUtils.getExtrasDebugInformationsWithKillerManagerAction(
                            killerManagerActionList
                        ),
                        actionType = actionType
                    )
            }
            return if (killerManagerActionList.isNotEmpty()) {
                // Intent found killerManagerActionType succeed
                killerManagerActionList
            } else {
                // Intent not found killerManagerActionType failed
                emptyList()
            }
        } else {
            // device not found killerManagerActionType failed
            return emptyList()
            /* LogUtils.e(KillerManager.class.getName(), "DEVICE NOT FOUND" + "SYSTEM UTILS \n" +
                        SystemUtils.getDefaultDebugInformation());*/
        }
    }

    /**
     * Execute the killerManagerActionType
     *
     * @param activity                the current activity
     * @param killerManagerActionType the wanted killerManagerActionType to execute
     * @return true : killerManagerActionType succeed; false killerManagerActionType failed
     */
    fun doAction(
        activity: FragmentActivity,
        killerManagerActionType: KillerManagerActionType
    ): Boolean =
        activity.doAction(
            killerManagerActionType,
            currentNbActions.getValue(killerManagerActionType)
        )

    /**
     * Execute the killerManagerActionType
     *
     * @param killerManagerActionType the wanted killerManagerActionType to execute
     * @return true : [killerManagerActionType] succeed; false [killerManagerActionType] failed
     */
    private fun FragmentActivity.doAction(
        killerManagerActionType: KillerManagerActionType,
        index: Int
    ): Boolean {
        // Avoid main app to crash when intent denied by using try catch
        try {
            val intentList = getIntentFromActionType(killerManagerActionType)
            if (intentList.isNotEmpty() && intentList.size > index) {
                val intent = intentList[index]
                if (ActionUtils.isIntentAvailable(packageManager, intent)) {
                    startActivityForResult(intent, requestCodeKillerManagerAction)
                    // Intent found killerManagerActionType succeed
                    return true
                }
            }
        } catch (e: Exception) {
            // Exception handle killerManagerActionType failed
            LogUtils.e(KillerManager::class.java.name, e.message)
            return false
        }

        return false
    }

    /**
     * Return the intent for a specific killerManagerActionType
     *
     * @param killerManagerActionType the wanted killerManagerActionType
     * @return the intent
     */
    private fun Context.getIntentFromActionType(
        killerManagerActionType: KillerManagerActionType
    ): List<Intent> {
        val device = DevicesManager.getDevice()
        this@KillerManager.device = device

        if (device != null) {
            val intentList: List<Intent> = when (killerManagerActionType) {
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
                // Intent found killerManagerActionType succeed
                intentList
            } else {
                LogUtils.indentNotFound(
                    packageManager = packageManager,
                    device = device,
                    extraDebugInfo = ActionUtils.getExtrasDebugInformations(intentList),
                    actionType = killerManagerActionType
                )
                // Intent not found killerManagerActionType failed
                emptyList()
            }
        } else {
            // device not found killerManagerActionType failed
            return emptyList()
            /* LogUtils.e(KillerManager.class.getName(), "DEVICE NOT FOUND" + "SYSTEM UTILS \n" +
                        SystemUtils.getDefaultDebugInformation());*/
        }
    }

    fun onActivityResult(
        activity: FragmentActivity,
        killerManagerActionType: KillerManagerActionType,
        requestCode: Int
    ) {
        if (requestCode == requestCodeKillerManagerAction) {
            currentNbActions[killerManagerActionType]?.let {
                currentNbActions[killerManagerActionType] = it + 1
            }

            val intentList = activity.getIntentFromActionType(killerManagerActionType)
            val actionIndex = currentNbActions[killerManagerActionType]
            if (intentList.isNotEmpty() && actionIndex != null && intentList.size > actionIndex)
                activity.doAction(
                    killerManagerActionType,
                    actionIndex
                )
            else
            // reset if no more intent
                currentNbActions[killerManagerActionType] = 0
        }
    }

    private const val requestCodeKillerManagerAction = 52000

    /*private void onActivityResultForDialog(Activity activity, KillerManagerActionType killerManagerAction, int requestCode) {
        if (requestCode == requestCodeKillerManagerAction) {
            int value = currentNbActions.get(killerManagerAction);
            value++;
            currentNbActions.put(killerManagerAction, value);
            List<Intent> intentList = getIntentFromActionType(activity, killerManagerAction);
            if (intentList == null || intentList.isEmpty() || intentList.size() <= currentNbActions.get(killerManagerAction)) {
                // reset if no more intent
                currentNbActions.put(killerManagerAction, 0);
            }
        }
    }*/
}
