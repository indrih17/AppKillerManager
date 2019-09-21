package com.thelittlefireman.appkillermanager_exemple

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thelittlefireman.appkillermanager.managers.KillerManager
import com.thelittlefireman.appkillermanager.models.KillerManagerActionType
import com.thelittlefireman.appkillermanager.ui.DialogKillerManager
import com.thelittlefireman.appkillermanager.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var actionType: KillerManagerActionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LogUtils.registerLogCustomListener(object : LogUtils.LogCustomListener {
            override fun i(tag: String, message: String) {
                // Custom Log
            }

            override fun w(tag: String, message: String) {
                // Custom log
            }

            override fun e(tag: String, message: String?) {
                // Custom Log
            }
        })

        powerSavingManagerButton.setOnClickListener {
            val actionType = KillerManagerActionType.ActionPowerSaving
                .also { actionType = it }

            if (idByDialog.isChecked)
                startDialog(actionType)
            else
                KillerManager.doAction(this, actionType)
        }
        autoStartManagerButton.setOnClickListener {
            val actionType = KillerManagerActionType.ActionAutoStart
                .also { actionType = it }

            if (idByDialog.isChecked)
                startDialog(actionType)
            else
                KillerManager.doAction(this, actionType)
        }
        notificationManagerButton.setOnClickListener {
            val actionType = KillerManagerActionType.ActionNotifications
                .also { actionType = it }

            if (idByDialog.isChecked)
                startDialog(actionType)
            else
                KillerManager.doAction(this, actionType)
        }
    }

    private fun startDialog(action: KillerManagerActionType) =
        DialogKillerManager(
            activity = this,
            killerManagerActionTypeList = listOf(action),
            titleMessage = "startDialog"
        )
            .show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        KillerManager.onActivityResult(this, actionType, requestCode)
    }
}
