package com.chartiq.sdk;

import org.json.JSONObject;

public interface OnDrawingChangedCallback {
    void execute(JSONObject json);
}
