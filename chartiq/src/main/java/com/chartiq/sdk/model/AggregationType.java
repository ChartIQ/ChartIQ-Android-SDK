package com.chartiq.sdk.model;

public enum AggregationType {
    HEIKINASHI("heikinashi"),
    KAGI("kagi"),
    RENKO("renko"),
    RANGEBARS("rangebars"),
    PANDF("pandf");

    private String value;

    AggregationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
