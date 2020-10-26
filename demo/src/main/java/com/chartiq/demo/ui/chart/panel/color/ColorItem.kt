package com.chartiq.demo.ui.chart.panel.color

import androidx.annotation.ColorInt

data class ColorItem(
    @ColorInt
    val color: Int,
    val isSelected: Boolean = false
)
