package com.chartiq.sdk;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ChartIQWebClient {
    WebView chartIQWebView;

    public WebView getChartIQWebView() {
        return chartIQWebView;
    }

    @SuppressLint("JavascriptInterface")
    private void setup() {
        WebSettings settings = chartIQWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        chartIQWebView.addJavascriptInterface(this, "promises");
        chartIQWebView.addJavascriptInterface(this, "QuoteFeed");
    }
}
