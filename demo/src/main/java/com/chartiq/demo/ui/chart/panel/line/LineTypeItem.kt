package com.chartiq.demo.ui.chart.panel.line

import androidx.annotation.DrawableRes
import com.chartiq.sdk.model.drawingtool.LineType

data class LineTypeItem(
    val lineType: LineType,
    val lineWidth: Int,
    @DrawableRes
    val iconRes: Int,
    val isSelected: Boolean = false
)
