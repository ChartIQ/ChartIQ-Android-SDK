package com.chartiq.sdk.model.drawingtool.drawingmanager

import com.chartiq.sdk.model.drawingtool.DrawingTool

/**
 * An implementation of DrawingManager that checks which parameters and settings a drawing tool supports
 */
class ChartIQDrawingManager : DrawingManager {

    /**
     * Checks if a drawing tool supports `fill color` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
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
            DrawingTool.TREND_LINE -> true
            else -> false
        }
    }

    /**
     * Checks if a drawing tool supports `line color` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingLineColor(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL -> false
            else -> true
        }
    }

    /**
     * Checks if a drawing tool supports `line type` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingLineType(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.NO_TOOL -> false
            else -> true
        }
    }

    /**
     * Checks if a drawing tool supports `settings` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingSettings(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.NO_TOOL,
            DrawingTool.MEASURE -> false
            else -> true
        }
    }

    /**
     * Checks if a drawing tool supports `font` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingFont(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ANNOTATION,
            DrawingTool.CALLOUT,
            DrawingTool.ELLIOTT_WAVE,
            DrawingTool.TREND_LINE -> true
            else -> false
        }
    }

    /**
     * Checks if a drawing tool supports `axis label` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingAxisLabel(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.CROSSLINE,
            DrawingTool.HORIZONTAL_LINE,
            DrawingTool.VERTICAL_LINE -> true
            else -> false
        }
    }

    /**
     * Checks if a drawing tool supports `deviations` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingDeviations(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.AVERAGE_LINE,
            DrawingTool.REGRESSION_LINE -> true
            else -> false
        }
    }

    /**
     * Checks if a drawing tool supports `Fibonacci` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingFibonacci(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.FIB_ARC,
            DrawingTool.FIB_FAN,
            DrawingTool.FIB_PROJECTION,
            DrawingTool.FIB_RETRACEMENT -> true
            else -> false
        }
    }

    /**
     * Checks if a drawing tool supports `Elliot wave` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    override fun isSupportingElliottWave(drawingTool: DrawingTool): Boolean {
        return when (drawingTool) {
            DrawingTool.ELLIOTT_WAVE -> true
            else -> false
        }
    }
}
