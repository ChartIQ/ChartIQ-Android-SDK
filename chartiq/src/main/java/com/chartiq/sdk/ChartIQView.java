package com.chartiq.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chartiq.sdk.model.AggregationType;
import com.chartiq.sdk.model.DataMethod;
import com.chartiq.sdk.model.DrawingTool;
import com.chartiq.sdk.model.OHLCParams;
import com.chartiq.sdk.model.Study;
import com.chartiq.sdk.scriptmanager.ChartIQScriptManager;
import com.chartiq.sdk.scriptmanager.ScriptManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChartIQView extends WebView implements ChartIQ, JavaScriptHandler {

    private DataSource dataSource;
    private ScriptManager scriptManager;
    private HashMap<String, Boolean> talkbackFields;

    public ChartIQView(Context context) {
        super(context);
        init();
    }

    public ChartIQView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartIQView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scriptManager = new ChartIQScriptManager();
    }

    @SuppressLint("JavascriptInterface")
    public void start(final String chartIQUrl, final OnStartCallback onStartCallback) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        addJavascriptInterface(this, "parameters");
        addJavascriptInterface(this, "promises");
        addJavascriptInterface(this, "QuoteFeed");
        loadUrl(chartIQUrl);
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                executeJavascript("nativeQuoteFeed(parameters, cb)");
                executeJavascript("addDrawingListener()");
                executeJavascript("addLayoutListener()");

                if (onStartCallback != null) {
                    onStartCallback.onStart();
                }
            }
        });
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setDataMethod(DataMethod method, String symbol) {
        DataMethod dataMethod;
        if (method == null) {
            dataMethod = DataMethod.PUSH;
        } else {
            dataMethod = method;
        }

        if (dataMethod == DataMethod.PULL) {
            // TODO: 26.08.20 review pull handling
            Log.d(getClass().getSimpleName(), "If you want to add a quotefeed please do so in your javascript code.");
        } else {
            executeJavascript(scriptManager.getSetDataMethodScript(symbol));
        }
    }

    @Override
    public void setSymbol(String symbol) {
//        if (mAccessibilityManager.isEnabled() && mAccessibilityManager.isTouchExplorationEnabled()) {
//            executeJavascript("accessibilityMode()");
//        }
//        this.invoke("newChart", toastCallback, symbol);
//        this.invoke("dateFromTick", null, 1);
//        executeJavascript("callNewChart(\"" + symbol + "\");", null);
        executeJavascript(scriptManager.getSetDataMethodScript(symbol));
        executeJavascript(scriptManager.getDateFromTickScript());
        executeJavascript(scriptManager.getSetSymbolScript(symbol));

    }

    // TODO: 26.08.20 remove
    @Override
    public void setShowDebugInfo(boolean showDebugInfo) {
    }

    // JavascriptInterface
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    @JavascriptInterface
    public void layoutChange(JSONObject json) {
        // TODO: 26.08.20 Add appropriate implementation
    }

    @Override
    @JavascriptInterface
    public void drawingChange(JSONObject json) {
        // TODO: 26.08.20 Add appropriate implementation
    }

    @Override
    @JavascriptInterface
    public void pullInitialData(final String symbol, int period, String interval, String start, String end, Object meta,
                                final String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol == null ? "" : symbol);
        params.put("period", period);
        params.put("interval", interval);
        params.put("start", start == null ? "" : start);
        params.put("end", end == null ? "" : end);
        params.put("meta", meta);

        if (dataSource != null) {
            dataSource.pullInitialData(params, new DataSourceCallback() {
                @Override
                public void execute(OHLCParams[] data) {
                    invokePullCallback(id, data);
                }
            });
        }
    }

    @Override
    @JavascriptInterface
    public void pullUpdate(final String symbol, int period, String interval, String start, Object meta,
                           final String callbackId) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol == null ? "" : symbol);
        params.put("period", period);
        params.put("interval", interval);
        params.put("start", start == null ? "" : start);
        params.put("meta", meta);

        if (dataSource != null) {
            dataSource.pullUpdateData(params, new DataSourceCallback() {
                @Override
                public void execute(OHLCParams[] data) {
                    invokePullCallback(callbackId, data);
                }
            });
        }
    }

    @Override
    @JavascriptInterface
    public void pullPagination(final String symbol, int period, String interval, String start, String end, Object meta,
                               final String callbackId) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol", symbol == null ? "" : symbol);
        params.put("period", period);
        params.put("interval", interval);
        params.put("start", start == null ? "" : start);
        params.put("end", end == null ? "" : end);
        params.put("meta", meta);

        if (dataSource != null) {
            dataSource.pullPaginationData(params, new DataSourceCallback() {
                @Override
                public void execute(OHLCParams[] data) {
                    invokePullCallback(callbackId, data);
                }
            });
        }
    }

    private void invokePullCallback(String callbackId, OHLCParams[] data) {
        String json = new Gson().toJson(data);
        executeJavascript("parseData('" + json + "', \"" + callbackId + "\");");
    }

    private void executeJavascript(final String script) {
        executeJavascript(script, null);
    }

    private void executeJavascript(final String script, final ValueCallback<String> callback) {
        evaluateJavascript(script, callback);
    }

    @Override
    public void enableCrosshairs() {
        executeJavascript(scriptManager.getEnableCrosshairsScript());
    }

    @Override
    public void disableCrosshairs() {
        executeJavascript(scriptManager.getDisableCrosshairsScript());
    }

    @Override
    public void setPeriodicity(int period, String interval, String timeUnit) {
        executeJavascript(scriptManager.getSetPeriodicityScript(period, interval, timeUnit));
    }

    @Override
    public void enableDrawing(final DrawingTool type) {
        executeJavascript(scriptManager.getEnableDrawingScript(type));
    }

    @Override
    public void disableDrawing() {
        executeJavascript(scriptManager.getDisableDrawingScript());
    }

    @Override
    public void clearDrawing() {
        executeJavascript(scriptManager.getClearDrawingScript());
    }

    @Override
    public void setAggregationType(AggregationType aggregationType) {
        executeJavascript(scriptManager.getSetAggregationTypeScript(aggregationType));
    }

    @Override
    public void setChartType(String chartType) {
        executeJavascript(scriptManager.getSetChartTypeScript(chartType));
    }

    @Override
    public void setChartScale(String scale) {
        executeJavascript(scriptManager.getSetChartScaleScript(scale));
    }

    @Override
    public void removeStudy(String studyName) {
        executeJavascript(scriptManager.getRemoveStudyScript(studyName));
    }

    @Override
    public void setDrawingParameter(String parameter, String value) {
        executeJavascript(scriptManager.getSetDrawingParameterScript(parameter, value), null);
    }

    @Override
    public void addStudy(Study study, boolean firstLoad) {
        Map<String, Object> inputs = study.getInputs();
        Map<String, Object> outputs = study.getOutputs();
        Map<String, Object> parameters = study.getParameters();
        if (firstLoad) {
            inputs = null;
            outputs = null;
        }
        String script;
        if (study.getType() == null) {
            script = scriptManager.getAddStudyScript(study.getShortName(), inputs, outputs, parameters);
        } else {
            script = scriptManager.getAddStudyScript(study.getType(), inputs, outputs, parameters);
        }
        executeJavascript(script);
    }

    @Override
    public Promise<Study[]> getStudyList() {
        String script = "getStudyList();";
        final Promise<Study[]> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {

            public void onReceiveValue(String value) {
                promise.setResult(new Gson().fromJson(value, Study[].class));
            }
        });
        return promise;
    }


    @Override
    public Promise<Study[]> getActiveStudies() {
        String script = "getActiveStudies();";
        final Promise<Study[]> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {

            public void onReceiveValue(String value) {
                if (value.equalsIgnoreCase("null")) {
                    value = "[]";
                }
                promise.setResult(new Gson().fromJson(value, Study[].class));
            }
        });
        return promise;
    }

    @Override
    public Promise<String> getStudyInputParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"inputs\");";
        final Promise<String> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(value);
            }
        });
        return promise;
    }

    @Override
    public Promise<String> getStudyOutputParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"outputs\");";
        final Promise<String> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(value);
            }
        });
        return promise;
    }

    @Override
    public Promise<String> getStudyParameters(String studyName) {
        String script = "getStudyParameters(\"" + studyName + "\" , \"parameters\");";
        final Promise<String> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(value);
            }
        });
        return promise;
    }

    @Override
    public void setTalkbackFields(HashMap<String, Boolean> talkbackFields) {
        this.talkbackFields = talkbackFields;
    }
}
