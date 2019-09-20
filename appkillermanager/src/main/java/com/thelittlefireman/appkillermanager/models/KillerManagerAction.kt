package com.thelittlefireman.appkillermanager.models

import android.content.Intent
import androidx.annotation.DrawableRes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list

@Serializer(forClass = Intent::class)
object IntentSerializer : KSerializer<Intent>

@Serializable
class KillerManagerAction(
    val actionType: KillerManagerActionType = KillerManagerActionType.ActionEmpty,

    @field:DrawableRes
    val helpImages: List<Int> = emptyList(),

    val intentActionList: List<@Serializable(IntentSerializer::class) Intent> = emptyList()
) {
    companion object {
        private val json = Json(JsonConfiguration.Stable)

        fun toJson(killerManagerAction: KillerManagerAction): String =
            json
                .toJson(serializer(), killerManagerAction)
                .toString()

        fun fromJson(jsonMessage: String): KillerManagerAction =
            json.parse(serializer(), jsonMessage)

        fun toJsonList(killerManagerActionList: List<KillerManagerAction>): String =
            json
                .toJson(serializer().list, killerManagerActionList)
                .toString()

        fun fromJsonList(jsonMessage: String): List<KillerManagerAction> =
            json.parse(serializer().list, jsonMessage)
    }
}
