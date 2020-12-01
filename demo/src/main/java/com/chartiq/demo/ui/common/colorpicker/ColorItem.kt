package com.chartiq.demo.ui.common.colorpicker

import androidx.annotation.ColorInt

data class ColorItem(
    @ColorInt
    val color: Int,
    val isSelected: Boolean = false
)
