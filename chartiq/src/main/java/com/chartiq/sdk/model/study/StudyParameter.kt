package com.chartiq.sdk.model.study

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A base class of active study parameter
 * @property heading A user-friendly name of the parameter
 * @property name A name of a parameter to be used as identifier of a parameter
 * @property parameterType A parameter type
 */
sealed class StudyParameter : Parcelable {
    abstract val heading: String
    abstract val name: String
    abstract val parameterType: StudyParameterType

    /**
     * A text parameter of active study [Study]
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default [String] value  of text parameter
     * @property value A current [String] value of text parameter
     */
    @Parcelize
    data class Text(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: String,
        val value: String
    ) : StudyParameter()

    /**
     * A number parameter of active study [Study]
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default [Double] value of text parameter
     * @property value A current [Double] value of text parameter
     */
    @Parcelize
    data class Number(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: Double,
        val value: Double
    ) : StudyParameter()

    /**
     * A color parameter of active study [Study]
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default color string value of a parameter
     * @property value A current color string value of a parameter. A format is <code>#RRGGBB</code>
     */
    @Parcelize
    data class Color(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: String,
        val value: String
    ) : StudyParameter()

    /**
     * A  parameter of active study [Study] that contains a number and a color
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default [Double] value of a parameter
     * @property value A current [Double] value of text a parameter
     * @property defaultColor A default color string value of a parameter
     * @property color A current color string value of a parameter. A format is <code>#RRGGBB</code>
     */
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

    /**
     * A boolean parameter of active study [Study]
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default boolean value of a parameter
     * @property value A current boolean value of a parameter
     */
    @Parcelize
    data class Checkbox(
        override val heading: String,
        override val name: String,
        override val parameterType: StudyParameterType,
        val defaultValue: Boolean,
        val value: Boolean
    ) : StudyParameter()

    /**
     * A parameter of active study [Study] that has a limited list of possible options
     * @property heading A user-friendly name of the parameter
     * @property name A name of a parameter to be used as identifier of a parameter
     * @property parameterType A parameter type
     * @property defaultValue A default string value of a parameter
     * @property value A current string value of a parameter
     * @property options A list of possible values
     */
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

    /**
     * A set of postfixes supported by the library
     */
    enum class StudyParameterNamePostfix {
        Enabled,
        Value,
        Color
    }
}
