package com.chartiq.sdk.model.drawingtool.drawingmanager

import com.chartiq.sdk.model.drawingtool.DrawingTool

interface DrawingManager {

    fun isSupportingFillColor(drawingTool: DrawingTool): Boolean

    fun isSupportingLineColor(drawingTool: DrawingTool): Boolean

    fun isSupportingLineType(drawingTool: DrawingTool): Boolean

    fun isSupportingSettings(drawingTool: DrawingTool): Boolean

    fun isSupportingFont(drawingTool: DrawingTool): Boolean

    fun isSupportingAxisLabel(drawingTool: DrawingTool): Boolean

    fun isSupportingDeviations(drawingTool: DrawingTool): Boolean

    fun isSupportingFibonacci(drawingTool: DrawingTool): Boolean

    fun isSupportingElliottWave(drawingTool: DrawingTool): Boolean
}