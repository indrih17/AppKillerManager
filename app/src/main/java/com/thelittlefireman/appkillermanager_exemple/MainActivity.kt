package com.thelittlefireman.appkillermanager_exemple

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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

        Log.i("MainActivity", Build.BRAND)
        Log.i("MainActivity", Build.MANUFACTURER)
        Log.i("MainActivity", Build.FINGERPRINT)

        LogUtils.registerLogCustomListener(object : LogUtils.LogCustomListener {
            override fun i(tag: String, message: String) {
                // Custom Log
            }

            override fun e(tag: String, message: String?) {
                // Custom Log
            }
        })
        powerSavingManagerButton.setOnClickListener {
            if (idByDialog.isChecked) {
                val actionType = KillerManagerActionType.ActionPowerSaving
                    .also { actionType = it }

                startDialog(actionType)
            } else {
                KillerManager.doAction(this@MainActivity, actionType)
            }
        }
        autoStartManagerButton.setOnClickListener {
            if (idByDialog.isChecked) {
                val actionType = KillerManagerActionType.ActionAutoStart
                    .also { actionType = it }

                startDialog(actionType)
            } else {
                KillerManager.doAction(this@MainActivity, actionType)
            }
        }
        notificationManagerButton.setOnClickListener {
            if (idByDialog.isChecked) {
                val actionType = KillerManagerActionType.ActionNotifications
                    .also { actionType = it }

                startDialog(actionType)
            } else {
                KillerManager.doAction(this@MainActivity, actionType)
            }
        }
    }

    fun startDialog(action: KillerManagerActionType) =
        DialogKillerManager(
            activity = this,
            killerManagerActionTypeList = listOf(action),
            titleMessage = "startDialog"
        )
            .show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        KillerManager.onActivityResult(this@MainActivity, actionType, requestCode)
    }
}
