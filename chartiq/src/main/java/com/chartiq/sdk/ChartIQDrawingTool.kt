package com.chartiq.sdk

import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.drawingtool.DrawingParameterType
import com.chartiq.sdk.model.drawingtool.DrawingTool

interface ChartIQDrawingTool {

    fun clearDrawing()

    fun enableDrawing(type: DrawingTool)

    fun disableDrawing()

    fun setDrawingParameter(parameter: String, value: String)

    fun setDrawingParameter(parameter: DrawingParameterType, value: String)

    fun getDrawingParameters(tool: DrawingTool, callback: OnReturnCallback<Map<String, Any>>)

    fun deleteDrawing()

    fun cloneDrawing()

    fun manageLayer(layer: ChartLayer)

    fun undoDrawing(callback: OnReturnCallback<Boolean>)

    fun redoDrawing(callback: OnReturnCallback<Boolean>)

    fun restoreDefaultDrawingConfig(tool: DrawingTool, all: Boolean)
}
