package com.chartiq.sdk;

import android.webkit.JavascriptInterface;

import org.json.JSONObject;

public interface JavaScriptHandler {

    @JavascriptInterface
    void layoutChange(JSONObject json);

    @JavascriptInterface
    void drawingChange(JSONObject json);

    @JavascriptInterface
    void pullInitialData(final String symbol,
                         int period,
                         String interval,
                         String start,
                         String end,
                         Object meta,
                         final String id);

    @JavascriptInterface
    void pullUpdate(final String symbol,
                    int period,
                    String interval,
                    String start,
                    Object meta,
                    final String callbackId);

    @JavascriptInterface
    void pullPagination(final String symbol,
                        int period,
                        String interval,
                        String start,
                        String end,
                        Object meta,
                        final String callbackId);
}
