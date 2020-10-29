package com.chartiq.sdk

import com.chartiq.sdk.model.drawingtool.DrawingTool

object DrawingToolSettingsManager {

    fun isSupportingFillColor(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ARROW,
            DrawingTool.CHANNEL,
            DrawingTool.CALLOUT,
            DrawingTool.CHECK,
            DrawingTool.CROSS,
            DrawingTool.ELLIPSE,
            DrawingTool.FIB_ARC,
            DrawingTool.FIB_FAN,
            DrawingTool.FIB_PROJECTION,
            DrawingTool.FIB_RETRACEMENT,
            DrawingTool.FIB_TIME_ZONE,
            DrawingTool.FOCUS,
            DrawingTool.GANN_FAN,
            DrawingTool.GARTLEY,
            DrawingTool.HEART,
            DrawingTool.QUADRANT_LINES,
            DrawingTool.RECTANGLE,
            DrawingTool.SPEED_RESISTANCE_ARC,
            DrawingTool.SPEED_RESISTANCE_LINE,
            DrawingTool.STAR,
            DrawingTool.TIME_CYCLE,
            DrawingTool.TIRONE_LEVELS,
            DrawingTool.TREND_LINE -> true
            else -> false
        }
    }

    fun isSupportingLineColor(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL -> false
            else -> true
        }
    }

    fun isSupportingLineType(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.NO_TOOL -> false
            else -> true
        }
    }

    fun isSupportingSettings(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL,
            DrawingTool.MEASURE -> false
            else -> true
        }
    }

    fun isSupportingFont(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.CALLOUT,
            DrawingTool.ELLIOTT_WAVE,
            DrawingTool.TREND_LINE -> true
            else -> false
        }
    }

    fun isSupportingAxisLabel(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.CROSSLINE,
            DrawingTool.HORIZONTAL_LINE,
            DrawingTool.VERTICAL_LINE -> true
            else -> false
        }
    }

    fun isSupportingDeviations(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.REGRESSION_LINE -> true
            else -> false
        }
    }

    fun isSupportingFibonacci(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.FIB_ARC,
            DrawingTool.FIB_FAN,
            DrawingTool.FIB_PROJECTION,
            DrawingTool.FIB_RETRACEMENT -> true
            else -> false
        }
    }

    fun isSupportingElliottWave(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ELLIOTT_WAVE -> true
            else -> false
        }
    }
}
