package com.thelittlefireman.appkillermanager_example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.*
import com.thelittlefireman.appkillermanager.managers.DevicesManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.ui.ActionDialogCreator

class MainActivity : AppCompatActivity() {
    private val alertCreator: ActionDialogCreator?

    init {
        alertCreator = DevicesManager
            .getDevice()
            .fold(
                ifLeft = {
                    onFailure(it)
                    null
                },
                ifRight = { device ->
                    ActionDialogCreator(device, this, ::onFailure)
                }
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alertCreator?.run {
            showDialogForAction(
                KillerManagerActionType.ActionAutoStart,
                R.string.auto_start_message
            )

            showDialogForAction(
                KillerManagerActionType.ActionPowerSaving,
                R.string.battery_optimization_message
            )

            showDialogForAction(
                KillerManagerActionType.ActionNotifications,
                R.string.notification_message
            )
        }
    }

    private fun onFailure(failure: Failure) =
        when (failure) {
            is InternalFail -> {
            }
            is UnknownDeviceFail -> {
            }
            is IntentFailure -> handleIntentFail(failure)
        }

    private fun handleIntentFail(intentFailure: IntentFailure) =
        when (intentFailure) {
            is IntentNotAvailableFail -> {
            }
            is IntentListForActionNotFoundFail -> {
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        alertCreator?.onActivityResult(requestCode)
    }
}
