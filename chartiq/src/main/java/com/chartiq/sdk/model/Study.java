package com.chartiq.sdk.model;

import java.io.Serializable;
import java.util.Map;

public class Study implements Serializable{
    private Map<String, Object> attributes;
    private Double centerline;
    private Boolean customRemoval;
    private Boolean deferUpdate;
    private String display;
    private Map<String, Object> inputs;
    private String name;
    private Map<String, Object> outputs;
    private Boolean overlay;
    private Map<String, Object> parameters;
    private String range;
    private String shortName;
    private Boolean underlay;
    private Map<String, Object> yAxis;
    private String type;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Double getCenterline() {
        return centerline;
    }

    public void setCenterline(Double centerline) {
        this.centerline = centerline;
    }

    public Boolean getCustomRemoval() {
        return customRemoval;
    }

    public void setCustomRemoval(Boolean customRemoval) {
        this.customRemoval = customRemoval;
    }

    public Boolean getDeferUpdate() {
        return deferUpdate;
    }

    public void setDeferUpdate(Boolean deferUpdate) {
        this.deferUpdate = deferUpdate;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public Boolean getOverlay() {
        return overlay;
    }

    public void setOverlay(Boolean overlay) {
        this.overlay = overlay;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getUnderlay() {
        return underlay;
    }

    public void setUnderlay(Boolean underlay) {
        this.underlay = underlay;
    }

    public Map<String, Object> getyAxis() {
        return yAxis;
    }

    public void setyAxis(Map<String, Object> yAxis) {
        this.yAxis = yAxis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equality = super.equals(obj);
        if(!equality && obj instanceof Study){
            return this.shortName != null && this.shortName.equals(((Study) obj).shortName);
        }
        return equality;
    }
}
