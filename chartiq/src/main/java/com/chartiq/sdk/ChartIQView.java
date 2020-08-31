package com.chartiq.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
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

import java.util.HashMap;
import java.util.Map;

public class ChartIQView extends WebView implements ChartIQ, JavaScriptHandler {

    private static final String JAVASCRIPT_INTERFACE_QUOTE_FEED = "QuoteFeed";

    private DataSource dataSource;
    private ScriptManager scriptManager;
    private HashMap<String, Boolean> talkbackFields;
    private AccessibilityManager mAccessibilityManager;

    public ChartIQView(Context context) {
        super(context);
        init(context);
    }

    public ChartIQView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartIQView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scriptManager = new ChartIQScriptManager();
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @SuppressLint("JavascriptInterface")
    public void start(final String chartIQUrl, final OnStartCallback onStartCallback) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        addJavascriptInterface(this, JAVASCRIPT_INTERFACE_QUOTE_FEED);
        loadUrl(chartIQUrl);
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                executeJavascript(scriptManager.getDetermineOSScript());
                executeJavascript(scriptManager.getNativeQuoteFeedScript());
                executeJavascript(scriptManager.getAddDrawingListenerScript());
                executeJavascript(scriptManager.getAddLayoutListenerScript());

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
        if (mAccessibilityManager.isEnabled() && mAccessibilityManager.isTouchExplorationEnabled()) {
            executeJavascript(scriptManager.getSetAccessibilityModeScript());
        }
        executeJavascript(scriptManager.getSetDataMethodScript(symbol));
        executeJavascript(scriptManager.getDateFromTickScript());
        executeJavascript(scriptManager.getSetSymbolScript(symbol));
    }

    @Override
    @JavascriptInterface
    public void layoutChange(String json) {
        // TODO: 26.08.20 Add appropriate implementation
    }

    @Override
    @JavascriptInterface
    public void drawingChange(String json) {
        // TODO: 26.08.20 Add appropriate implementation
    }

    @Override
    @JavascriptInterface
    public void pullInitialData(final String symbol, int period, String interval, String start, String end, Object meta,
                                final String id) {
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_SYMBOL, symbol == null ? "" : symbol);
        params.put(PARAM_PERIOD, period);
        params.put(PARAM_INTERVAL, interval);
        params.put(PARAM_START, start == null ? "" : start);
        params.put(PARAM_END, end == null ? "" : end);
        params.put(PARAM_META, meta);

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
        params.put(PARAM_SYMBOL, symbol == null ? "" : symbol);
        params.put(PARAM_PERIOD, period);
        params.put(PARAM_INTERVAL, interval);
        params.put(PARAM_START, start == null ? "" : start);
        params.put(PARAM_META, meta);

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
        params.put(PARAM_SYMBOL, symbol == null ? "" : symbol);
        params.put(PARAM_PERIOD, period);
        params.put(PARAM_INTERVAL, interval);
        params.put(PARAM_START, start == null ? "" : start);
        params.put(PARAM_END, end == null ? "" : end);
        params.put(PARAM_META, meta);

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
        executeJavascript(scriptManager.getParseDataScript(data, callbackId));
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
    public void getStudyList(final OnReturnCallback<Study[]> callback) {
        executeJavascript(scriptManager.getGetStudyListScript(), new ValueCallback<String>() {
            public void onReceiveValue(String value) {
                callback.onReturn(new Gson().fromJson(value, Study[].class));
            }
        });
    }

    @Override
    public void getActiveStudies(final OnReturnCallback<Study[]> callback) {
        executeJavascript(scriptManager.getGetActiveStudiesScript(), new ValueCallback<String>() {

            public void onReceiveValue(String value) {
                if (value.equalsIgnoreCase("null")) {
                    value = "[]";
                }
                callback.onReturn(new Gson().fromJson(value, Study[].class));
            }
        });
    }

    @Override
    public void getStudyInputParameters(String studyName, final OnReturnCallback<String> callback) {
        executeJavascript(scriptManager.getGetStudyInputParametersScript(studyName), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                callback.onReturn(value);
            }
        });
    }

    @Override
    public void getStudyOutputParameters(String studyName, final OnReturnCallback<String> callback) {
        executeJavascript(scriptManager.getGetStudyOutputParametersScript(studyName), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                callback.onReturn(value);
            }
        });
    }

    @Override
    public void getStudyParameters(String studyName, final OnReturnCallback<String> callback) {
        executeJavascript(scriptManager.getGetStudyParametersScript(studyName), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                callback.onReturn(value);
            }
        });
    }

    @Override
    public void setTalkbackFields(HashMap<String, Boolean> talkbackFields) {
        this.talkbackFields = talkbackFields;
    }
}
