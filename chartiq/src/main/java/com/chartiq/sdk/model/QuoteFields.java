package com.chartiq.sdk.model;

public enum QuoteFields {
    DATE("Date"),
    CLOSE("Close"),
    OPEN("Open"),
    HIGH("High"),
    LOW("Low"),
    VOLUME("Volume");

    private String quoteField;

    QuoteFields(String quoteField) {
        this.quoteField = quoteField;
    }

    public String value() {
        return quoteField;
    }
}
