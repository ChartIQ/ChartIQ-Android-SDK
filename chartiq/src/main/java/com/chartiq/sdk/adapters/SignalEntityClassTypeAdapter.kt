package com.chartiq.sdk.adapters

import com.chartiq.sdk.model.signal.ConditionEntity
import com.chartiq.sdk.model.signal.MarkerOptionEntity
import com.chartiq.sdk.model.signal.SignalEntity
import com.chartiq.sdk.model.study.StudyEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


internal class SignalEntityClassTypeAdapter : JsonDeserializer<SignalEntity> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        ctx: JsonDeserializationContext
    ): SignalEntity {
        val decodeObj: JsonObject = json.asJsonObject
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val signalEntity: SignalEntity = gson.fromJson(json, SignalEntity::class.java)
        val conditions = decodeObj["conditions"].asJsonArray
        val conditionsList = mutableListOf<ConditionEntity>()
        val standardGson = Gson()
        for (condition in conditions) {
            val array = condition.asJsonArray
            val rightIndicator = if (array[2].isJsonNull) {
                null
            } else {
                array[2].asString
            }
            val markerOption = try {
                standardGson.fromJson(
                    array[4].asJsonObject,
                    MarkerOptionEntity::class.java
                )
            } catch (e: Exception) {
                null
            }
            conditionsList.add(
                ConditionEntity(
                    leftIndicator = array[0].asString,
                    signalOperator = array[1].asString,
                    rightIndicator = rightIndicator,
                    color = array[3].asString,
                    markerOption = markerOption
                )
            )
        }
        val typeToken = object : TypeToken<Map<String, Object>>() {}.type
        val outputs: Map<String, Object> = if (decodeObj["outputs"].isJsonArray) {
            emptyMap()
        } else {
            val single: Map<String, Object> = gson.fromJson(decodeObj["outputs"], typeToken)
            single
        }

        val studyName = decodeObj["studyName"].asString
        return signalEntity.copy(
            conditions = conditionsList,
            studyName = studyName,
            studyEntity = StudyEntity(
                attributes = emptyMap(),
                centerLine = 0.0,
                customRemoval = false,
                deferUpdate = false,
                display = "",
                parsed_inputs = emptyList(),
                parsed_outputs = listOf(outputs),
                name = decodeObj["studyName"].asString,
                overlay = false,
                parameters = emptyMap(),
                range = "",
                shortName = decodeObj["studyName"].asString,
                type = studyName.substringBeforeLast(ZERO_WIDTH_NON_JOINER),
                underlay = false,
                yAxis = emptyMap()
            )
        )
    }

    companion object {
        private const val ZERO_WIDTH_NON_JOINER = '\u200C'
    }
}
