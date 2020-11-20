package com.chartiq.demo.ui.common.linepicker

import androidx.annotation.DrawableRes
import com.chartiq.sdk.model.drawingtool.LineType

data class LineItem(
    val lineType: LineType,
    val lineWidth: Int,
    @DrawableRes
    val iconRes: Int,
    val isSelected: Boolean = false
)
