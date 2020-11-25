package com.chartiq.demo.ui.chart.drawingtools.list.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.chartiq.sdk.model.DrawingTool

// TODO: 07.11.20 Make var variables read-only vals
data class DrawingToolItem(
    val tool: DrawingTool,
    @DrawableRes
    val iconRes: Int,
    @StringRes
    val nameRes: Int,
    val category: DrawingToolCategory,
    val section: DrawingToolSection,
    var isSelected: Boolean = false,
    var isStarred: Boolean = false
)
