package com.chartiq.sdk.model.drawingtool

import com.chartiq.sdk.OnReturnCallback
import com.chartiq.sdk.model.ChartLayer

interface ChartIQDrawingTool {

    /**
     * Clears all the drawings from the chart canvas
     */
    fun clearDrawing()

    /**
     * Activates a selected drawing [DrawingTool]
     * @param tool A [DrawingTool] to be selected
     */
    fun enableDrawing(tool: DrawingTool)

    /**
     * Deactivates drawing mode
     */
    fun disableDrawing()

    /**
     * Sets a value for the drawing tool parameter
     * @param parameter A parameter to update the value of
     * @param value A new value to be set to the parameter
     */
    fun setDrawingParameter(parameterName: String, value: String)

    /**
     * Sets a value for the drawing tool parameter
     * @param parameter A [DrawingParameterType] to update the value of
     * @param value A new value to be set to the parameter
     */
    fun setDrawingParameter(parameter: DrawingParameterType, value: String)

    /**
     * Get a map of current parameters and settings for the requested drawing tool [tool]
     * @param tool A [DrawingTool] of parameters and settings to get
     * @param callback A callback [OnReturnCallback] to subscribe to to get the map of the parameters and settings
     */
    fun getDrawingParameters(tool: DrawingTool, callback: OnReturnCallback<Map<String, Any>>)

    /**
     * Deletes the drawing that is selected on the chart
     */
    fun deleteDrawing()

    /**
     * Clones the drawing that is selected on the chart
     */
    fun cloneDrawing()

    /**
     * Changes the layer of the drawing that is selected on the chart
     * @param layer  A [ChartLayer] to assign to the drawing
     */
    fun manageLayer(layer: ChartLayer)

    /**
     * Undoes the last drawing change
     * @param callback A callback [OnReturnCallback] to subscribe to to get approval that the latest change was undone
     */
    fun undoDrawing(callback: OnReturnCallback<Boolean>)

    /**
     * Redoes the last drawing change
     * @param callback A callback [OnReturnCallback] to subscribe to to get approval that the latest change was redone
     */
    fun redoDrawing(callback: OnReturnCallback<Boolean>)

    /**
     * Restores the drawing tool to its default settings
     * @param tool A [DrawingTool] to restore the settings of
     * @param all Set to true if you want to restore all drawings configs
     */
    fun restoreDefaultDrawingConfig(tool: DrawingTool, all: Boolean)
}
