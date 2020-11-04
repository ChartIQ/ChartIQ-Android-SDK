package com.chartiq.demo.ui.chart.drawingtools.list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.chartiq.sdk.model.drawingtool.DrawingTool

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