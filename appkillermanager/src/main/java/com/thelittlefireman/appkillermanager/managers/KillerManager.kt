package com.thelittlefireman.appkillermanager.managers

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.thelittlefireman.appkillermanager.BuildConfig
import com.thelittlefireman.appkillermanager.devices.DeviceBase
import com.thelittlefireman.appkillermanager.models.KillerManagerAction
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType.*
import com.thelittlefireman.appkillermanager.utils.ActionUtils
import com.thelittlefireman.appkillermanager.utils.LogUtils
import com.thelittlefireman.appkillermanager.utils.SystemUtils

object KillerManager {
    var device: DeviceBase? = DevicesManager.getDevice()
        private set

    private val currentNbActions = hashMapOf(
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
        val device = DevicesManager.getDevice()
        this.device = device
        if (device != null) {
            val killerManagerActionList = ArrayList<KillerManagerAction>()
            for (actionType in killerManagerActionTypeList) {
                when (actionType) {
                    ActionAutoStart -> {
                        val actionAutoStart: KillerManagerAction? =
                            device.getActionAutoStart(context)
                        if (device.isActionAutoStartAvailable(context)
                            && actionAutoStart != null
                            && ActionUtils.isAtLeastOneIntentAvailable(context, actionAutoStart)
                        )
                            killerManagerActionList.add(actionAutoStart)
                    }

                    ActionPowerSaving -> {
                        val actionPowerSaving: KillerManagerAction? =
                            device.getActionPowerSaving(context)
                        if (device.isActionPowerSavingAvailable(context)
                            && actionPowerSaving != null
                            && ActionUtils.isAtLeastOneIntentAvailable(context, actionPowerSaving)
                        )
                            killerManagerActionList.add(actionPowerSaving)
                    }

                    ActionNotifications -> {
                        val actionNotification = device.getActionNotification(context)
                        if (device.isActionNotificationAvailable(context)
                            && actionNotification != null
                            && ActionUtils.isAtLeastOneIntentAvailable(context, actionNotification)
                        )
                            killerManagerActionList.add(actionNotification)
                    }

                    ActionEmpty -> Unit
                }

                // do nothing
                if (killerManagerActionList.isEmpty()) {
                    LogUtils.e(
                        KillerManager::class.java.name, "INTENT NOT FOUND :" +
                                ActionUtils.getExtrasDebugInformationsWithKillerManagerAction(
                                    killerManagerActionList
                                ) +
                                "LibraryVersionName :" + BuildConfig.VERSION_NAME +
                                "LibraryVersionCode :" + BuildConfig.VERSION_CODE +
                                "KillerManagerActionType \n" + actionType.name + "SYSTEM UTILS \n" +
                                SystemUtils.defaultDebugInformation + "DEVICE \n" +
                                device.getExtraDebugInformations(context)
                    )
                }
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
     * Return the intent for a specific killerManagerActionType
     *
     * @param context                 the current context
     * @param killerManagerActionType the wanted killerManagerActionType
     * @return the intent
     */
    private fun getIntentFromActionType(
        context: Context,
        killerManagerActionType: KillerManagerActionType
    ): List<Intent> {
        val device = DevicesManager.getDevice()
        this.device = device
        if (device != null) {
            val intentList: List<Intent> = when (killerManagerActionType) {
                ActionAutoStart -> device.getActionAutoStart(context)?.intentActionList
                    ?: emptyList()
                ActionPowerSaving -> device.getActionPowerSaving(context)?.intentActionList
                    ?: emptyList()
                ActionNotifications -> device.getActionNotification(context)?.intentActionList
                    ?: emptyList()
                ActionEmpty -> emptyList()
            }
            if (intentList.isNotEmpty()
                && ActionUtils.isAtLeastOneIntentAvailable(context, intentList)
            ) {
                // Intent found killerManagerActionType succeed
                return intentList
            } else {
                LogUtils.e(
                    KillerManager::class.java.name, "INTENT NOT FOUND :" +
                            ActionUtils.getExtrasDebugInformations(intentList) +
                            "LibraryVersionName :" + BuildConfig.VERSION_NAME +
                            "LibraryVersionCode :" + BuildConfig.VERSION_CODE +
                            "KillerManagerActionType \n" + killerManagerActionType.name + "SYSTEM UTILS \n" +
                            SystemUtils.defaultDebugInformation + "DEVICE \n" +
                            device.getExtraDebugInformations(context)
                )
                // Intent not found killerManagerActionType failed
                return emptyList()
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
        doAction(
            activity,
            killerManagerActionType,
            currentNbActions.getValue(killerManagerActionType)
        )

    /**
     * Execute the killerManagerActionType
     *
     * @param activity                the current activity
     * @param killerManagerActionType the wanted killerManagerActionType to execute
     * @return true : killerManagerActionType succeed; false killerManagerActionType failed
     */
    private fun doAction(
        activity: FragmentActivity,
        killerManagerActionType: KillerManagerActionType,
        index: Int
    ): Boolean {
        // Avoid main app to crash when intent denied by using try catch
        try {
            val intentList = getIntentFromActionType(activity, killerManagerActionType)
            if (intentList.isNotEmpty() && intentList.size > index) {
                val intent = intentList[index]
                if (ActionUtils.isIntentAvailable(activity, intent)) {
                    activity.startActivityForResult(intent, requestCodeKillerManagerAction)
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

    fun onActivityResult(
        activity: FragmentActivity,
        killerManagerActionType: KillerManagerActionType,
        requestCode: Int
    ) {
        if (requestCode == requestCodeKillerManagerAction) {
            currentNbActions[killerManagerActionType]?.inc()

            val intentList = getIntentFromActionType(activity, killerManagerActionType)
            val managerAction = currentNbActions[killerManagerActionType]
            if (intentList.isNotEmpty() && managerAction != null && intentList.size > managerAction)
                doAction(
                    activity,
                    killerManagerActionType,
                    managerAction
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
