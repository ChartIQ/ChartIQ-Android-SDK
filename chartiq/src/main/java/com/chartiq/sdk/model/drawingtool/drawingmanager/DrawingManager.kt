package com.chartiq.sdk.model.drawingtool.drawingmanager

import com.chartiq.sdk.model.drawingtool.DrawingTool

interface DrawingManager {

    /**
     * Checks if a drawing tool supports `fill color` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingFillColor(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `line color` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingLineColor(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `line type` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingLineType(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `settings` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingSettings(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `font` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingFont(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `axis label` setting
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingAxisLabel(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `deviations` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingDeviations(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `Fibonacci` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingFibonacci(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `VolumeProfile` settings
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingElliottWave(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `Elliot wave` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingVolumeProfile(drawingTool: DrawingTool): Boolean

    /**
     * Checks if a drawing tool supports `Show Callout` parameters
     * @param drawingTool A [DrawingTool] to be checked for the support
     * @return true if the drawing tool supports the setting, false if not
     */
    fun isSupportingShowCallOut(drawingTool: DrawingTool): Boolean
}
