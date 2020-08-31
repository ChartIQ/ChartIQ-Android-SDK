package com.chartiq.sdk;

import android.webkit.JavascriptInterface;

public interface JavaScriptHandler {
    public static final String PARAM_SYMBOL = "symbol";
    public static final String PARAM_PERIOD = "period";
    public static final String PARAM_INTERVAL = "interval";
    public static final String PARAM_START = "start";
    public static final String PARAM_END = "end";
    public static final String PARAM_META = "meta";

    @JavascriptInterface
    void layoutChange(String json);

    @JavascriptInterface
    void drawingChange(String json);

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
