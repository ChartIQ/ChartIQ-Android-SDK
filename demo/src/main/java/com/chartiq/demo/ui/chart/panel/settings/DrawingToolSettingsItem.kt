package com.chartiq.demo.ui.chart.panel.settings

import android.os.Parcelable
import androidx.annotation.StringRes
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.sdk.model.drawingtool.DrawingParameterType
import com.chartiq.sdk.model.drawingtool.LineType
import kotlinx.android.parcel.Parcelize

sealed class DrawingToolSettingsItem : Parcelable {

    @Parcelize
    data class ChooseValue(
        @StringRes
        val title: Int,
        val secondaryText: String = "",
        val param: String,
        val valueList: List<OptionItem>,
        val isMultipleSelection: Boolean = false,
        val hasCustomValueSupport: Boolean = false,
        val hasNegativeValueSupport: Boolean = true
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Switch(
        @StringRes
        val title: Int,
        val checked: Boolean,
        val param: String
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Style(
        @StringRes
        val title: Int,
        val isBold: Boolean,
        val isItalic: Boolean,
        val weightParam: String = DrawingParameterType.WEIGHT.value,
        val styleParam: String = DrawingParameterType.STYLE.value
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Line(
        @StringRes
        val title: Int,
        val lineType: LineType,
        val lineWidth: Int,
        val lineTypeParam: String = DrawingParameterType.LINE_TYPE.value,
        val lineWidthParam: String = DrawingParameterType.LINE_WIDTH.value
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Color(
        @StringRes
        val title: Int,
        val color: String,
        val param: String
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Deviation(
        @StringRes
        val title: Int,
        val settings: List<DrawingToolSettingsItem>
    ) : DrawingToolSettingsItem()

    @Parcelize
    data class Number(
        @StringRes
        val title: Int,
        val number: Int,
        val param: String = DrawingParameterType.PRICE_BUCKETS.value
    ) : DrawingToolSettingsItem()
}
