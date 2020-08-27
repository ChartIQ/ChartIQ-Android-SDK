package com.chartiq.sdk.model;

public enum DrawingParameter {
    FILL_COLOR("fillColor"),
    CURRENT_COLOR("currentColor"),
    PATTERN("pattern"),
    LINE_WIDTH("lineWidth");

    String value;

    DrawingParameter(String value) {
        this.value = value;
    }
}
