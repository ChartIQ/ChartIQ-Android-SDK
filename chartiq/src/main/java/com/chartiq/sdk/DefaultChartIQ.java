package com.chartiq.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chartiq.sdk.model.Study;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.chartiq.sdk.Util.buildArgumentStringFromArgs;

public class DefaultChartIQ extends WebView implements ChartIQWebView {
    private static final String CHART_IQ_JS_OBJECT = "stxx";

    private AccessibilityManager mAccessibilityManager;
    private DataSource dataSource;
    private boolean showDebugInfo;
    private HashMap<String, Boolean> talkbackFields = new HashMap<>();

    public DefaultChartIQ(Context context) {
        super(context);
        init(context);
    }

    public DefaultChartIQ(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultChartIQ(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void start(final String chartIQUrl, final CallbackStart callbackStart) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDomStorageEnabled(true);
        addJavascriptInterface(this, "promises");
        addJavascriptInterface(this, "QuoteFeed");
        loadUrl(chartIQUrl);
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                executeJavascript("nativeQuoteFeed(parameters, cb)");
                executeJavascript("addDrawingListener()");
                executeJavascript("addLayoutListener()");

                if (callbackStart != null) {
                    callbackStart.onStart();
                }
            }
        });
    }

    @Override
    public void setSymbol(String symbol) {
        if (mAccessibilityManager.isEnabled() && mAccessibilityManager.isTouchExplorationEnabled()) {
            executeJavascript("accessibilityMode()");
        }
        invoke("dateFromTick", null, 1);
        executeJavascript("callNewChart(\"" + symbol + "\");", null);
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
            System.out.println("If you want to add a quotefeed please do so in your javascript code.");
        } else {
            this.invoke("newChart", null, symbol);
        }
    }

    @Override
    public void setShowDebugInfo(boolean showDebugInfo) {
        this.showDebugInfo = showDebugInfo;
    }

    @Override
    public void enableCrosshairs() {
        executeJavascript("enableCrosshairs(true);", null);
    }

    @Override
    public void disableCrosshairs() {
        executeJavascript("enableCrosshairs(false);", null);
    }

    @Override
    public void setPeriodicity(int period, String interval, String timeUnit) {
        if (timeUnit == null) {
            timeUnit = "minute";
        }
        String args = buildArgumentStringFromArgs(period, interval, timeUnit);
        executeJavascript("setPeriodicity(" + period + ", \"" + interval + "\", \"" + timeUnit + "\");", null);
    }

    @Override
    public void enableDrawing(final DrawingType type) {
        // TODO: 17.08.20 Move fun name
        final String functionName = "changeVectorType";
        String script = CHART_IQ_JS_OBJECT + "." + functionName + "(" + type.value() + ")";
        evaluateJavascript(script, null);
    }

    // TODO: 17.08.20 Remove the following method
    public void enableDrawing(String type) {
        invoke("changeVectorType", null, type);
    }

    @Override
    public void disableDrawing() {
        enableDrawing(DrawingType.NONE);
    }

    @Override
    public void clearDrawing() {
        this.invoke("clearDrawings", null);
    }

    @Override
    public Promise<Study[]> getStudyList() {
        String script = "getStudyList();";
        final Promise<Study[]> promise = new Promise<>();
        executeJavascript(script, new ValueCallback<String>() {
            @Override
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
            @Override
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
    public void setAggregationType(AggregationType aggregationType) {
        this.invoke("setAggregationType", null, aggregationType.value());
    }

    // TODO: 18.08.20 Remove
    public void setAggregationType(String aggregationType) {
        this.invoke("setAggregationType", null, aggregationType);
    }

    @Override
    public void setChartType(String chartType) {
        invoke("setChartType", null, chartType);
    }

    @Override
    public void setChartScale(String scale) {
        this.invoke("setChartScale", null, scale);
    }

    @Override
    public void removeStudy(String studyName) {
        executeJavascript("removeStudy(\"" + studyName + "\");", null);

    }

    @Override
    public void addStudy(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters) {
        String script = "addStudy(" + buildArgumentStringFromArgs(studyName, inputs, outputs, parameters) + ")";
        executeJavascript(script, null);
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
        if (study.getType() == null) {
            addStudy(study.getShortName(), inputs, outputs, parameters);
        } else {
            addStudy(study.getType(), inputs, outputs, parameters);
        }
    }

    @Override
    public void setDrawingParameter(String parameter, String value) {
        executeJavascript("setCurrentVectorParameters(" + parameter + ", " + value + ");", null);
    }

    @Override
    public void setTalkbackFields(HashMap<String, Boolean> talkbackFields) {
        this.talkbackFields = talkbackFields;
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

    private void executeJavascript(final String script) {
        executeJavascript(script, null);
    }

    private void executeJavascript(final String script, final ValueCallback<String> callback) {
        evaluateJavascript(script, callback);
    }

    private void invoke(final String functionName, final ValueCallback callback, final Object... args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String script = CHART_IQ_JS_OBJECT + "." + functionName + "(" + buildArgumentStringFromArgs(args) + ")";
                evaluateJavascript(script, callback);
            }
        };
        runOnUiThread(runnable);
    }


    private void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }
}
