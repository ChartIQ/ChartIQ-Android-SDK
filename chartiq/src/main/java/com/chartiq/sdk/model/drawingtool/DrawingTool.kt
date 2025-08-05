package com.chartiq.sdk.model.drawingtool

/**
 * An enumeration of available drawing tools
 */
enum class DrawingTool(
    /**
     * @suppress
     */
    val value: String?
) {
    ANNOTATION("annotation"),
    ARROW("arrowline"),
    AVERAGE_LINE("average"),
    CALLOUT("callout"),
    CHANNEL("channel"),
    CONTINUOUS_LINE("continuous"),
    CROSSLINE("crossline"),
    DOODLE("freeform"),
    ELLIOTT_WAVE("elliottwave"),
    ELLIPSE("ellipse"),
    FIB_ARC("fibarc"),
    FIB_FAN("fibfan"),
    FIB_PROJECTION("fibprojection"),
    FIB_RETRACEMENT("retracement"),
    FIB_TIME_ZONE("fibtimezone"),
    GANN_FAN("gannfan"),
    GARTLEY("gartley"),
    HORIZONTAL_LINE("horizontal"),
    LINE("line"),
    MEASURE("measure"),
    MEASUREMENTLINE("measurementline"),
    NO_TOOL(""),
    PITCHFORK("pitchfork"),
    QUADRANT_LINES("quadrant"),
    RAY("ray"),
    RECTANGLE("rectangle"),
    REGRESSION_LINE("regression"),
    SPEED_RESISTANCE_ARC("speedarc"),
    SPEED_RESISTANCE_LINE("speedline"),
    TIME_CYCLE("timecycle"),
    TIRONE_LEVELS("tirone"),
    TREND_LINE("trendline"),
    VERTICAL_LINE("vertical"),
    VOLUME_PROFILE("volumeprofile"),
    NONE(null)
}
