package com.chartiq.sdk.model;

public enum DrawingPattern {
    SOLID("solid"),
    DOTTED("dotted"),
    DASHED("dashed");

    String value;

    DrawingPattern(String value) {
        this.value = value;
    }
}
