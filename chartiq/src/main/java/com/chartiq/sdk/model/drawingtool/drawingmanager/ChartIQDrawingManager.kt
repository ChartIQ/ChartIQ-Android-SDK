package com.chartiq.sdk.model.drawingtool.drawingmanager

import com.chartiq.sdk.model.drawingtool.DrawingTool

/**
 * An implementation of DrawingManager that checks which parameters and settings a drawing tool supports
 */
class ChartIQDrawingManager : DrawingManager {
    override fun isSupportingFillColor(drawingTool: DrawingTool): Boolean {
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
            DrawingTool.MEASUREMENTLINE,
            DrawingTool.TREND_LINE -> true

            else -> false
        }
    }

    override fun isSupportingLineColor(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL -> false
            else -> true
        }
    }

    override fun isSupportingLineType(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.NO_TOOL -> false

            else -> true
        }
    }

    override fun isSupportingSettings(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL,
            DrawingTool.MEASURE -> false

            else -> true
        }
    }

    override fun isSupportingFont(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.CALLOUT,
            DrawingTool.ELLIOTT_WAVE,
            DrawingTool.TREND_LINE -> true

            else -> false
        }
    }

    override fun isSupportingAxisLabel(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.CROSSLINE,
            DrawingTool.HORIZONTAL_LINE,
            DrawingTool.MEASUREMENTLINE,
            DrawingTool.VERTICAL_LINE -> true

            else -> false
        }
    }

    override fun isSupportingDeviations(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.REGRESSION_LINE -> true

            else -> false
        }
    }

    override fun isSupportingFibonacci(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.FIB_ARC,
            DrawingTool.FIB_FAN,
            DrawingTool.FIB_PROJECTION,
            DrawingTool.FIB_RETRACEMENT -> true

            else -> false
        }
    }

    override fun isSupportingElliottWave(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ELLIOTT_WAVE -> true
            else -> false
        }
    }

    override fun isSupportingVolumeProfile(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.VOLUME_PROFILE -> true
            else -> false
        }
    }

    override fun isSupportingShowCallOut(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.TREND_LINE -> true
            else -> false
        }
    }
}
