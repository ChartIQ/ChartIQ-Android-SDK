package com.chartiq.sdk;

public enum DrawingType {
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

    DrawingType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
