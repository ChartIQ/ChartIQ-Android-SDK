package com.chartiq.sdk.adapters

import com.chartiq.sdk.model.study.StudySimplifiedEntity
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


internal class StudySimplifiedEntityClassTypeAdapter : JsonDeserializer<StudySimplifiedEntity> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        ctx: JsonDeserializationContext
    ): StudySimplifiedEntity {
        val decodeObj: JsonObject = json.asJsonObject
        val gson = Gson()
        val typeToken = object : TypeToken<Map<String, Object>>() {}.type
        val outputs: Map<String, Object> = if (decodeObj["outputs"].isJsonArray) {
            emptyMap()
        } else {
            val single: Map<String, Object> = gson.fromJson(decodeObj["outputs"], typeToken)
            single
        }
        return StudySimplifiedEntity(
            studyName = decodeObj["studyName"].asString,
            parsed_outputs = listOf(outputs),
            type = decodeObj["type"].asString
        )
    }
}
