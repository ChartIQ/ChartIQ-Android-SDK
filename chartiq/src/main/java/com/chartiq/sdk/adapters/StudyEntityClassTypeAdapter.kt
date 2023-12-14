package com.chartiq.sdk.adapters

import com.chartiq.sdk.model.study.StudyEntity
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


internal class StudyEntityClassTypeAdapter : JsonDeserializer<StudyEntity> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        ctx: JsonDeserializationContext
    ): StudyEntity {
        val decodeObj: JsonObject = json.asJsonObject
        val gson = Gson()
        val studyEntity: StudyEntity = gson.fromJson(json, StudyEntity::class.java)
        val typeToken = object : TypeToken<Map<String, Any>>() {}.type
        val outputs: Map<String, Any> = if (decodeObj["outputs"] == null) {
            emptyMap()
        } else
            if (decodeObj["outputs"].isJsonArray) {
                emptyMap()
            } else {
                val single: Map<String, Any> = gson.fromJson(decodeObj["outputs"], typeToken)
                single
            }
        val inputs: Map<String, Any> = if (decodeObj["inputs"] == null) {
            emptyMap()
        } else
            if (decodeObj["inputs"].isJsonArray) {
                emptyMap()
            } else {
                val single: Map<String, Any> = gson.fromJson(decodeObj["inputs"], typeToken)
                single
            }
        studyEntity.parsed_outputs = listOf(outputs)
        studyEntity.parsed_inputs = listOf(inputs)
        return studyEntity
    }
}
