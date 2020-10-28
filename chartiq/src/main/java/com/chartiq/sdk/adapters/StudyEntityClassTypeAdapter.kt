package com.chartiq.sdk.adapters

import com.chartiq.sdk.model.StudyEntity
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StudyEntityClassTypeAdapter : JsonDeserializer<StudyEntity> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        ctx: JsonDeserializationContext,
    ): StudyEntity {
        val decodeObj: JsonObject = json.asJsonObject
        val gson = Gson()
        val studyEntity: StudyEntity = gson.fromJson(json, StudyEntity::class.java)
        val typeToken = object : TypeToken<Map<String, Object>>() {}.type
        val values: Map<String, Object>
        values = if (decodeObj["outputs"].isJsonArray) {
            emptyMap()
        } else {
            val single: Map<String, Object> = gson.fromJson(decodeObj["outputs"], typeToken)
            single
        }
        studyEntity.my_outputs = listOf(values)
        return studyEntity
    }
}