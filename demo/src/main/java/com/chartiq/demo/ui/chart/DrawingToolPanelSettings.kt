package com.chartiq.demo.ui.chart

import com.chartiq.demo.network.model.PanelDrawingToolParameter
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.sdk.model.drawingtool.DrawingTool

data class DrawingToolPanelSettings(
    val drawingTool: DrawingTool,
    val parameter: PanelDrawingToolParameter?,
    val instrumentItems: List<InstrumentItem>
)