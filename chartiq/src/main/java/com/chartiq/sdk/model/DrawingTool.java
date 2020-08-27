package com.chartiq.sdk.model;

public enum DrawingTool {
    ANNOTATION("annotation"),
    CHANNEL("channel"),
    DOODLE("freeform"),
    ELLIPSE("ellipse"),
    FIB_ARC("fibarc"),
    FIB_FAN("fibfan"),
    FIB_RETRACE("fibonacci"),
    FIB_TIMEZONE("fibtimezone"),
    GARTLEY("gartley"),
    HORIZONTAL_LINE("horizontal"),
    LINE("line"),
    PITCH_FORK("pitchfork"),
    RAY("ray"),
    RECTANGLE("rectangle"),
    SEGMENT("segment"),
    VERTICAL_LINE("vertical"),
    NONE("");

    private String value;

    DrawingTool(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
