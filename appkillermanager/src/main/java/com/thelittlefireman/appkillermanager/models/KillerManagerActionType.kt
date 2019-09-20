package com.thelittlefireman.appkillermanager.models

enum class KillerManagerActionType(private val action: String) {
    ActionAutoStart("ACTION_AUTOSTART"),
    ActionNotifications("ACTION_NOTIFICATIONS"),
    ActionPowerSaving("ACTION_POWERSAVING"),
    ActionEmpty("ACTION_EMPTY");

    override fun toString(): String = action
}