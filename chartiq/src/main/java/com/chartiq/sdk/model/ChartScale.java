package com.chartiq.sdk.model;

public enum ChartScale {
    LOG("log"),
    LINEAR("linear");

    String value;

    ChartScale(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
