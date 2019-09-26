package com.thelittlefireman.appkillermanager.models

enum class KillerManagerActionType(private val action: String) {
    ActionAutoStart("ACTION_AUTOSTART"),
    ActionNotifications("ACTION_NOTIFICATIONS"),
    ActionPowerSaving("ACTION_POWERSAVING");

    override fun toString(): String = action
}