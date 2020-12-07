package com.chartiq.demo.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class DrawingParameter(val value: String) : Parcelable {
    FILL_COLOR("fillColor"),
    LINE_COLOR("color"),
    LINE_TYPE("pattern"),
    LINE_WIDTH("lineWidth"),
    FONT("font"),
    FAMILY("family"),
    SIZE("size"),
    STYLE("style"),
    WEIGHT("weight"),
    NORMAL("normal"),
    ITALIC("italic"),
    BOLD("bold"),
    BOLD_OFF("300"),
    AXIS_LABEL("axisLabel"),
    PARAMETERS("parameters"),
    FIBS("fibs"),
    SHOW_LINES("showLines"),
    WAVE_PARAMETERS("waveParameters"),
    WAVE_TEMPLATE("waveTemplate"),
    IMPULSE("impulse"),
    CORRECTIVE("corrective"),
    DECORATION("decoration"),
    LEVEL("level"),
    DISPLAY("display"),
    ACTIVE_1("active1"),
    ACTIVE_2("active2"),
    ACTIVE_3("active3"),
    COLOR_1("color1"),
    COLOR_2("color2"),
    COLOR_3("color3"),
    LINE_WIDTH_1("lineWidth1"),
    LINE_WIDTH_2("lineWidth2"),
    LINE_WIDTH_3("lineWidth3"),
    PATTERN_1("pattern1"),
    PATTERN_2("pattern2"),
    PATTERN_3("pattern3")
}
