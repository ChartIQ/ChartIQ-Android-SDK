package com.chartiq.sdk.old;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.chartiq.sdk.DataSource;
import com.chartiq.sdk.Promise;
import com.chartiq.sdk.model.Hud;
import com.chartiq.sdk.model.OHLCParams;
import com.chartiq.sdk.model.QuoteFields;
import com.chartiq.sdk.model.Study;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

// TODO: Remove the following class
public class ChartIQWV extends WebView {
    public String chartiqSdkVersion = "3.2.0";
    public String applicationVersion = "3.2.0";
    private static final String CHART_IQ_JS_OBJECT = "stxx";
    private boolean showDebugInfo;
    private DataSource dataSource;
    private ValueCallback EMPTY_CALLBACK = new ValueCallback() {
        @Override
        public void onReceiveValue(Object value) {
            // STUB
        }
    };
    private ValueCallback toastCallback = new ValueCallback() {
        @Override
        public void onReceiveValue(Object value) {
            if (showDebugInfo) {
            }
        }
    };

    private ArrayList<Promise> promises = new ArrayList<>();
    private HashMap<String, Boolean> talkbackFields = new HashMap<String, Boolean>();

    GestureDetector gd;
    private AccessibilityManager mAccessibilityManager;

    public ChartIQWV(Context context) {
        super(context);
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        gd = new GestureDetector(context, sogl);
    }

    public ChartIQWV(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        gd = new GestureDetector(context, sogl);
    }

    public ChartIQWV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        gd = new GestureDetector(context, sogl);
    }

    private boolean swipeLeft = false;
    private boolean swipeRight = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // normal touch events if not in accessibility mode
        if (!mAccessibilityManager.isEnabled() && !mAccessibilityManager.isTouchExplorationEnabled()) {
            return super.onTouchEvent(event);
        }

        gd.onTouchEvent(event);
        if (swipeLeft) {
            swipeLeft = false;

            executeJavascript("accessibilitySwipe();", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        swipeGesture(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        } else if (swipeRight) {
            swipeRight = false;
            executeJavascript("accessibilitySwipe(RIGHT_SWIPE);", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        swipeGesture(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void swipeGesture(String value) {
        String[] fieldsArray = value.split(Pattern.quote("||"));

        if (fieldsArray.length == 6) {
            // the below is very clunky, find a better way in the future
            // maybe first idea of passing in fields to library instead
            // of getting everything back
            String date = fieldsArray[0];
            String close = fieldsArray[1];
            String open = fieldsArray[2];
            String high = fieldsArray[3];
            String low = fieldsArray[4];
            String volume = fieldsArray[5];

            String selectedFields = "";

            if (talkbackFields.get(QuoteFields.DATE.value())) {
                selectedFields += ", " + date;
            }

            if (talkbackFields.get(QuoteFields.CLOSE.value())) {
                selectedFields += ", " + close;
            }

            if (talkbackFields.get(QuoteFields.OPEN.value())) {
                selectedFields += ", " + open;
            }

            if (talkbackFields.get(QuoteFields.HIGH.value())) {
                selectedFields += ", " + high;
            }

            if (talkbackFields.get(QuoteFields.LOW.value())) {
                selectedFields += ", " + low;
            }

            if (talkbackFields.get(QuoteFields.VOLUME.value())) {
                selectedFields += ", " + volume;
            }

            this.announceForAccessibility(selectedFields);
        } else {
            this.announceForAccessibility(value);
        }
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            float diffY = event1.getY() - event2.getY();
            float diffX = event1.getX() - event2.getX();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 1) {
                    if (diffX > 0) {
                        swipeLeft = true;
                    } else {
                        swipeRight = true;
                    }
                }
            }

            return true;
        }
    };

    public Promise<String> getChartName() {
        final Promise<String> promise = new Promise<>();
        executeJavascript("getSymbol()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(value.substring(1, value.length() - 1));
            }
        });
        return promise;
    }

    /**
     * Checks to see if the chart is finished loading
     *
     * @return Promise<Boolean>
     */
    public Promise<Boolean> isChartAvailable() {
        final Promise<Boolean> promise = new Promise<>();
        executeJavascript("isChartAvailable()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(new Boolean(value));
            }
        });
        return promise;
    }


    public void setSymbolObject(JSONObject symbolObject) {
        this.invoke("newChart", toastCallback, symbolObject);
        String symbol = "";
        try {
            symbol = symbolObject.getString("symbol");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pushData(String symbol, OHLCParams[] data) {
        this.invoke("newChart", toastCallback, symbol, data);
    }

    public void pushUpdate(OHLCParams[] data) {
        String json = new Gson().toJson(data);
        executeJavascript("parseData('" + json + "');");
    }

    // rework
    public void addComparison(String symbol, String hexColor, Boolean isComparison) {
        executeJavascript("addSeries(\"" + symbol + "\", \"" + hexColor + "\", " + true + ");", toastCallback);
        // this.invoke("addSeries", symbol, toastCallback);
    }

    public void removeComparison(String symbol) {
        this.invoke("removeSeries", toastCallback, symbol);
    }

    public void clearChart() {
        this.invoke("destroy", toastCallback);
    }

    /**
     * Removes all the active studies
     */
    public void removeAllStudies() {
        executeJavascript("removeAllStudies();", toastCallback);
    }


    /**
     * Checks to see if the crosshair is turned on or off
     *
     * @return Promise<Boolean>
     */
    public Promise<Boolean> isCrosshairsEnabled() {
        final Promise<Boolean> promise = new Promise<>();
        String script = CHART_IQ_JS_OBJECT + ".layout.crosshair";
        executeJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                promise.setResult(new Boolean(value));
            }
        });
        return promise;
    }

    public Promise<Hud> getCrosshairsHUDDetail() {
        String script = "getHudDetails();";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Promise<Hud> promise = new Promise<>();
            executeJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if (value.equalsIgnoreCase("null")) {
                        value = "";
                    }
                    promise.setResult(new Gson().fromJson(value, Hud.class));
                }
            });
            return promise;
        } else {
            final Promise<Hud> promise = new Promise<>();
            promises.add(promise);
            loadUrl("javascript:" + script);
            loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", JSON.stringify(result))");
            return promise;
        }
    }

    public void setStyle(String object, String parameter, String value) {
        this.invoke("setStyle", toastCallback, object, parameter, value);
    }

    /**
     * Sets the theme for the chart: 'day', 'night', 'none' 'none' is there if
     * the user wants to use custom themes they created
     *
     * @param theme
     */
    public void setTheme(String theme) {
        executeJavascript("setTheme(\"" + theme + "\");", toastCallback);
    }

    @JavascriptInterface
    public void setPromiseResult(int id, String result) {
        Promise p = promises.get(id);
        Study[] studies = new Gson().fromJson(result, Study[].class);
        if (studies != null) {
            p.setResult(studies);
        } else {
            p.setResult(result);
        }
    }

    public Promise<Study[]> getStudyList() {
        String script = "getStudyList();";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Promise<Study[]> promise = new Promise<>();
            executeJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    promise.setResult(new Gson().fromJson(value, Study[].class));
                }
            });
            return promise;
        } else {
            final Promise<Study[]> promise = new Promise<>();
            promises.add(promise);
            loadUrl("javascript:" + script);
            loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", JSON.stringify(result))");
            return promise;
        }
    }

    public void setStudyInputParameter(String studyName, String parameter, String value) {
        String args = buildArgumentStringFromArgs(studyName, parameter, value);
        String script = "setStudyParameter(" + args + ", true);";
        executeJavascript(script, toastCallback);
    }

    public void setStudyOutputParameter(String studyName, String parameter, String value) {
        String args = buildArgumentStringFromArgs(studyName, parameter, value);
        String script = "setStudyParameter(" + args + ", false);";
        executeJavascript(script, toastCallback);
    }

    public Promise<String> getDrawingParameters(String drawingName) {
        String script = "getCurrentVectorParameters();";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Promise<String> promise = new Promise<>();
            executeJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    promise.setResult(value);
                }
            });
            return promise;
        } else {
            final Promise<String> promise = new Promise<>();
            promises.add(promise);
            loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
            return promise;
        }
    }

    /**
     * Execute function from specified object
     *
     * @param jsObject   object to execute function from
     * @param methodName javascript function to execute
     * @param args       arguments to pass to javascript function
     */
    private void invokeWithObject(final String jsObject, final String methodName, final Object... args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String script = jsObject + "." + methodName + "(" + buildArgumentStringFromArgs(args) + ")";
                ValueCallback callback = getCallBackFromArgs(args);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    evaluateJavascript(script, callback);
                } else {
                    loadUrl("javascript:" + script);
                }
            }
        };
        runOnUiThread(runnable);
    }

    /**
     * Change a css style on the chart
     *
     * @param args parameters that define what to change, must be put in order
     *             (selector, property, value) ex:
     *             changeChartStyle("stx_mountain_chart", "color", "blue");
     */
    public void changeChartStyle(final Object... args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String script = CHART_IQ_JS_OBJECT + ".setStyle(" + buildArgumentStringFromArgs(args) + ")";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    evaluateJavascript(script, null);
                } else {
                    loadUrl("javascript:" + script);
                }
            }
        };
        runOnUiThread(runnable);
    }

    /**
     * Change a property value on the chart
     *
     * @param property The property to change
     * @param value    The value to change
     */
    public void setChartProperty(final String property, final Object value) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String script = CHART_IQ_JS_OBJECT + ".chart." + property + "=" + buildArgumentStringFromArgs(value);
                System.out.println(script);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    evaluateJavascript(script, null);
                } else {
                    loadUrl("javascript:" + script);
                }
            }
        };
        runOnUiThread(runnable);
    }

    /**
     * Get a property value from the chart
     *
     * @param property
     * @return Promise that contains the value
     */
    public Promise<String> getChartProperty(final String property) {
        String script = CHART_IQ_JS_OBJECT + ".chart." + property;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Promise<String> promise = new Promise<>();
            executeJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    promise.setResult(value);
                }
            });
            return promise;
        } else {
            final Promise<String> promise = new Promise<>();
            promises.add(promise);
            loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
            return promise;
        }
    }

    /**
     * Change a property value on the chart engine
     *
     * @param property The property to change
     * @param value    The value to change
     */
    public void setEngineProperty(final String property, final Object value) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String script = CHART_IQ_JS_OBJECT + "." + property + "=" + buildArgumentStringFromArgs(value);
                System.out.println(script);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    evaluateJavascript(script, null);
                } else {
                    loadUrl("javascript:" + script);
                }
            }
        };
        runOnUiThread(runnable);
    }

    /**
     * Set a property value on the chart engine
     *
     * @param property
     * @return Promise that contains the value
     */
    public Promise<String> getEngineProperty(final String property) {
        String script = CHART_IQ_JS_OBJECT + "." + property;
        System.out.println(script);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Promise<String> promise = new Promise<>();
            System.out.println(script);
            executeJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    System.out.println(value);
                    promise.setResult(value);
                }
            });
            return promise;
        } else {
            final Promise<String> promise = new Promise<>();
            promises.add(promise);
            loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
            return promise;
        }
    }

    ValueCallback getCallBackFromArgs(Object[] args) {
        if (args == null || args.length == 0)
            return EMPTY_CALLBACK;

        Object lastArgument = args[args.length - 1];
        if (lastArgument instanceof ValueCallback) {
            return (ValueCallback) lastArgument;
        } else {
            return EMPTY_CALLBACK;
        }
    }

    String buildArgumentStringFromArgs(Object... args) {
        String s = new Gson().toJson(args);
        return s.substring(1, s.length() - 1);
    }
}
