package com.chartiq.sdk

import com.chartiq.sdk.model.ChartLayer
import com.chartiq.sdk.model.drawingtool.DrawingParameter
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.chartiq.sdk.model.drawingtool.DrawingToolParameters

interface ChartIQDrawingTool {

    fun clearDrawing()

    fun enableDrawing(type: DrawingTool)

    fun disableDrawing()

    fun setDrawingParameter(parameter: DrawingParameter, value: String)

    fun getDrawingParameters(tool: DrawingTool, callback: OnReturnCallback<DrawingToolParameters>)

    fun deleteDrawing()

    fun cloneDrawing()

    fun manageLayer(layer: ChartLayer)
}
