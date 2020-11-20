package com.chartiq.sdk.model.drawingtool

// TODO: 20.11.20 Discuss if it's possible to keep the DrawingParameter for Typed requests
enum class DrawingParameter(val value: String) {
    LINE_TYPE("pattern"),
    LINE_WIDTH("lineWidth"),
    COLOR("color"),
    FILL_COLOR("fillColor")
}
