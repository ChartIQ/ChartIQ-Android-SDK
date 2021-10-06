package com.chartiq.demo

import com.chartiq.demo.ui.chart.ChartViewState
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.sdk.model.drawingtool.DrawingTool
import kotlinx.coroutines.flow.Flow

interface ApplicationPrefs {

    val languageState: Flow<ChartIQLanguage>

    fun saveDrawingTool(tool: DrawingTool)

    fun getDrawingTool(): DrawingTool

    fun saveFavoriteDrawingTools(drawingToolsSet: Set<DrawingTool>)

    fun getFavoriteDrawingTools(): Set<DrawingTool>

    fun getApplicationId(): String

    fun setLanguage(language: ChartIQLanguage)

    fun setChartViewState(chartViewState: ChartViewState)

    fun getChartViewState(): ChartViewState
}

