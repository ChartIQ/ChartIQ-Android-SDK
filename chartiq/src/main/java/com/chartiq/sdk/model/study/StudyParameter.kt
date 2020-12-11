package com.chartiq.sdk.model.study

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class StudyParameter : Parcelable {
    abstract val heading: String
    abstract val name: String
    abstract val parameterType: StudyParameterType

    @Parcelize
    data class Text(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: String,
            val value: String
    ) : StudyParameter()

    @Parcelize
    data class Number(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: Double,
            val value: Double
    ) : StudyParameter()

    @Parcelize
    data class Color(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: String,
            val value: String
    ) : StudyParameter()

    @Parcelize
    data class TextColor(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: Double,
            val value: Double?,
            val defaultColor: String,
            val color: String?
    ) : StudyParameter()

    @Parcelize
    data class Checkbox(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: Boolean,
            val value: Boolean
    ) : StudyParameter()

    @Parcelize
    data class Select(
            override val heading: String,
            override val name: String,
            override val parameterType: StudyParameterType,
            val defaultValue: String,
            val value: String,
            val options: Map<String, String>
    ) : StudyParameter()

    companion object {
        const val AUTO_VALUE = "auto"
    }

    enum class StudyParameterNamePostfix {
        Enabled,
        Value,
        Color
    }
}



