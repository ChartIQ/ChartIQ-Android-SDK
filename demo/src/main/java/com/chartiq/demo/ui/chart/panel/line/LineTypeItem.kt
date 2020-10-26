package com.chartiq.demo.ui.chart.panel.line

import androidx.annotation.DrawableRes
import com.chartiq.sdk.model.LineType

data class LineTypeItem(
    val lineType: LineType,
    @DrawableRes
    val iconRes: Int,
    val isSelected: Boolean = false
)
