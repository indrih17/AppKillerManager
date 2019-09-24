package com.thelittlefireman.appkillermanager.models

import android.content.Intent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializer(forClass = Intent::class)
object IntentSerializer : KSerializer<Intent>

@Serializable
class KillerManagerAction(
    val actionType: KillerManagerActionType = KillerManagerActionType.ActionEmpty,

    val intentActionList: List<@Serializable(IntentSerializer::class) Intent> = emptyList()
) {
    fun toJson(): String =
        json
            .toJson(serializer(), this)
            .toString()

    companion object {
        private val json = Json(JsonConfiguration.Stable)

        fun fromJson(jsonMessage: String): KillerManagerAction =
            json.parse(serializer(), jsonMessage)
    }
}
