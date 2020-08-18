package com.chartiq.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chartiq.sdk.model.Hud;
import com.chartiq.sdk.model.OHLCChart;
import com.chartiq.sdk.model.Study;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChartIQ extends WebView {
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
				// Toast.makeText(getContext(), "response: " + value.toString(),
				// Toast.LENGTH_LONG).show();
			}
		}
	};

	private ArrayList<OnLayoutChangedCallback> onLayoutChanged = new ArrayList<>();
	private ArrayList<OnDrawingChangedCallback> onDrawingChanged = new ArrayList<>();
	private ArrayList<OnPullInitialDataCallback> onPullInitialData = new ArrayList<>();
	private ArrayList<OnPullUpdateCallback> onPullUpdate = new ArrayList<>();
	private ArrayList<OnPullPaginationCallback> onPullPagination = new ArrayList<>();
	private ArrayList<Promise> promises = new ArrayList<>();
	private HashMap<String, Boolean> talkbackFields = new HashMap<String, Boolean>();

	GestureDetector gd;
	private AccessibilityManager mAccessibilityManager;

	/**
	 *
	 * @param context
	 */
	public ChartIQ(Context context) {
		super(context);
		mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		gd = new GestureDetector(context, sogl);
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 */
	public ChartIQ(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		gd = new GestureDetector(context, sogl);
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public ChartIQ(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		gd = new GestureDetector(context, sogl);
	}

	private boolean swipeLeft = false;
	private boolean swipeRight = false;
	private static final int SWIPE_MIN_DISTANCE = 320;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

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

	/**
	 *
	 * @param value
	 */
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


	/**
	 *
	 * @param event
	 */
	public void addEvent(Event event) {

	}

	@SuppressLint("setJavaScriptEnabled")
	private void runChartIQ(final String chartIQUrl, final CallbackStart callbackStart) {
		ChartIQ.this.post(new Runnable() {
			@Override
			public void run() {
				getSettings().setJavaScriptEnabled(true);
				getSettings().setDomStorageEnabled(true);
				addJavascriptInterface(ChartIQ.this, "promises");
				addJavascriptInterface(ChartIQ.this, "QuoteFeed");
				loadUrl(chartIQUrl);
				setWebViewClient(new WebViewClient() {
					public void onPageFinished(WebView view, String url) {
						executeJavascript("nativeQuoteFeed(parameters, cb)", null);
						executeJavascript("addDrawingListener()");
						executeJavascript("addLayoutListener()");

						if (callbackStart != null) {
							callbackStart.onStart();
						}
					}
				});
			}
		});
	}

	/**
	 *
	 * @param chartIQUrl
	 * @param callbackStart
	 */
	public void start(final String chartIQUrl, final CallbackStart callbackStart) {
		runChartIQ(chartIQUrl, callbackStart);
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<OnLayoutChangedCallback> getOnLayoutChangedCallbacks() {
		return onLayoutChanged;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<OnDrawingChangedCallback> getOnDrawingChangedCallbacks() {
		return onDrawingChanged;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<OnPullInitialDataCallback> getOnPullInitialDataCallbacks() {
		return onPullInitialData;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<OnPullUpdateCallback> getOnPullUpdateCallbacks() {
		return onPullUpdate;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<OnPullPaginationCallback> getOnPullPaginationCallbacks() {
		return onPullPagination;
	}

	/**
	 *
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public interface DataSource {
		void pullInitialData(Map<String, Object> params, DataSourceCallback callback);

		void pullUpdateData(Map<String, Object> params, DataSourceCallback callback);

		void pullPaginationData(Map<String, Object> params, DataSourceCallback callback);
	}

	public interface DataSourceCallback {
		void execute(OHLCChart[] data);
	}

	/**
	 *
	 * @param method
	 * @param symbol
	 */
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
			this.invoke("newChart", toastCallback, symbol);
		}
		addEvent(new Event("CHIQ_setDataMethod").set("method", dataMethod == DataMethod.PULL ? "PULL" : "PUSH"));
	}

	/**
	 *
	 * @return
	 */
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

	/**
	 *
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		if (mAccessibilityManager.isEnabled() && mAccessibilityManager.isTouchExplorationEnabled()) {
			executeJavascript("accessibilityMode()");
		}
		//this.invoke("newChart", toastCallback, symbol);
		this.invoke("dateFromTick", toastCallback, 1);
		executeJavascript("callNewChart(\"" + symbol + "\");",
		toastCallback);
		addEvent(new Event("CHIQ_setSymbol").set("symbol", symbol));
	}

	/**
	 *
	 * @param symbolObject
	 */
	public void setSymbolObject(JSONObject symbolObject) {
		this.invoke("newChart", toastCallback, symbolObject);
		String symbol = "";
		try {
			symbol = symbolObject.getString("symbol");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		addEvent(new Event("CHIQ_setSymbol").set("symbolObject.symbol", symbol));
	}

	/**
	 *
	 * @param period
	 * @param interval
	 * @param timeUnit
	 */
	public void setPeriodicity(int period, String interval, String timeUnit) {
		if (timeUnit == null) {
			timeUnit = "minute";
		}
		String args = buildArgumentStringFromArgs(period, interval, timeUnit);
		executeJavascript("setPeriodicity(" + period + ", \"" + interval + "\", \"" + timeUnit + "\");", toastCallback);
		addEvent(new Event("CHIQ_setPeriodicity").set("periodicity", period).set("interval", interval));
	}

	/**
	 *
	 * @param symbol
	 * @param data
	 */
	public void pushData(String symbol, OHLCChart[] data) {
		this.invoke("newChart", toastCallback, symbol, data);
		addEvent(new Event("CHIQ_pushInitialData").set("symbol", symbol).set("data", data));
	}

	/**
	 *
	 * @param data
	 */
	public void pushUpdate(OHLCChart[] data) {
		String json = new Gson().toJson(data);
		executeJavascript("parseData('" + json + "');");
		addEvent(new Event("CHIQ_pushUpdate").set("data", data));
	}

	/**
	 *
	 * @param chartType
	 */
	public void setChartType(String chartType) {
		this.invoke("setChartType", toastCallback, chartType);
		addEvent(new Event("CHIQ_setChartType").set("chartType", chartType));
	}

	/**
	 *
	 * @param aggregationType
	 */
	public void setAggregationType(String aggregationType) {
		this.invoke("setAggregationType", toastCallback, aggregationType);
		addEvent(new Event("CHIQ_setAggregationType").set("aggregationType", aggregationType));
	}

	// rework
	public void addComparison(String symbol, String hexColor, Boolean isComparison) {
		executeJavascript("addSeries(\"" + symbol + "\", \"" + hexColor + "\", " + true + ");", toastCallback);
		// this.invoke("addSeries", symbol, toastCallback);
		addEvent(new Event("CHIQ_addComparison").set("symbol", symbol));
	}

	public void removeComparison(String symbol) {
		this.invoke("removeSeries", toastCallback, symbol);
		addEvent(new Event("CHIQ_removeComparison").set("symbol", symbol));
	}

	/**
	 *
	 */
	public void clearChart() {
		this.invoke("destroy", toastCallback);
		addEvent(new Event("CHIQ_clearChart"));
	}

	/**
	 *
	 * @param scale
	 */
	public void setChartScale(String scale) {
		this.invoke("setChartScale", toastCallback, scale);
		addEvent(new Event("CHIQ_setChartScale").set("scale", scale));
	}

	/**
	 *
	 * @param studyName
	 * @param inputs
	 * @param outputs
	 */
    public void addStudy(String studyName, Map<String, Object> inputs, Map<String, Object> outputs, Map<String, Object> parameters) {
        String script = "addStudy(" + buildArgumentStringFromArgs(studyName, inputs, outputs, parameters) + ")";
		executeJavascript(script, toastCallback);
		addEvent(new Event("CHIQ_addStudy").set("studyName", studyName));
	}

	/**
	 *
	 * @param study
	 */
	public void addStudy(Study study, boolean firstLoad) {
		Map<String, Object> inputs = study.inputs;
		Map<String, Object> outputs = study.outputs;
        Map<String, Object> parameters = study.parameters;
		if(firstLoad){
			inputs = null;
			outputs = null;
		}
		if (study.type == null) {
			addStudy(study.shortName, inputs, outputs, parameters);
		} else {
			addStudy(study.type, inputs, outputs, parameters);
		}
		addEvent(new Event("CHIQ_addStudy").set("studyName", study.name));
	}

	/**
	 * Removes the active study that matches the supplied studyName
	 * @param studyName
	 */
	public void removeStudy(String studyName) {
		executeJavascript("removeStudy(\"" + studyName + "\");", toastCallback);
		addEvent(new Event("CHIQ_removeStudy"));
	}

	/**
	 * Removes all the active studies
	 */
	public void removeAllStudies() {
		executeJavascript("removeAllStudies();", toastCallback);
		addEvent(new Event("CHIQ_removeAllStudies"));
	}

	/**
	 *
	 */
	public void enableCrosshairs() {
		executeJavascript("enableCrosshairs(true);", toastCallback);
		addEvent(new Event("CHIQ_enableCrosshairs"));
	}

	/**
	 * Checks to see if the crosshair is turned on or off
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

	/**
	 *
	 */
	public void disableCrosshairs() {
		executeJavascript("enableCrosshairs(false);", toastCallback);
		addEvent(new Event("CHIQ_disableCrosshairs"));
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

	/**
	 *
	 * @param type
	 */
	public void enableDrawing(String type) {
		invoke("changeVectorType", toastCallback, type);
		addEvent(new Event("CHIQ_enableDrawing"));
	}

	public void disableDrawing() {
		enableDrawing("");
	}

	public void clearDrawing() {
		this.invoke("clearDrawings", toastCallback);
	}

	public void setDrawingParameter(String parameter, String value) {
		executeJavascript("setCurrentVectorParameters(" + parameter + ", " + value + ");", toastCallback);
		addEvent(new Event("CHIQ_setDrawingParameter").set("parameter", parameter).set("value", value));
	}

	/**
	 *
	 * @param object
	 * @param parameter
	 * @param value
	 */
	public void setStyle(String object, String parameter, String value) {
		this.invoke("setStyle", toastCallback, object, parameter, value);
		addEvent(new Event("CHIQ_setStyle").set("style", parameter).set("value", value));
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

	/**
	 *
	 * @param id
	 * @param result
	 */
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

	/**
	 *
	 * @return
	 */
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
			addEvent(new Event("CHIQ_getStudyList"));
			return promise;
		} else {
			final Promise<Study[]> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:" + script);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", JSON.stringify(result))");
			addEvent(new Event("CHIQ_getStudyList"));
			return promise;
		}

	}

	/**
	 *
	 * @return
	 */
	public Promise<Study[]> getActiveStudies() {
		String script = "getActiveStudies();";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
		} else {
			final Promise<Study[]> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:" + script);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", JSON.stringify(result))");
			return promise;
		}
	}

	/**
	 *
	 * @param studyName
	 * @return
	 */
	public Promise<String> getStudyInputParameters(String studyName) {
		String script = "getStudyParameters(\"" + studyName + "\" , \"inputs\");";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Promise<String> promise = new Promise<>();
			executeJavascript(script, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					promise.setResult(value);
				}
			});
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:" + script);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", helper.inputs)");
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		}
	}

	/**
	 *
	 * @param studyName
	 * @return
	 */
	public Promise<String> getStudyOutputParameters(String studyName) {
		String script = "getStudyParameters(\"" + studyName + "\" , \"outputs\");";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Promise<String> promise = new Promise<>();
			executeJavascript(script, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					promise.setResult(value);
				}
			});
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:" + script);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", helper.outputs)");
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		}
	}

	/**
	 *
	 * @param studyName
	 * @return
	 */
	public Promise<String> getStudyParameters(String studyName) {
		String script = "getStudyParameters(\"" + studyName + "\" , \"parameters\");";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Promise<String> promise = new Promise<>();
			executeJavascript(script, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					promise.setResult(value);
				}
			});
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:" + script);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", helper.parameters");
			addEvent(new Event("CHIQ_getStudyParameters").set("studyName", studyName));
			return promise;
		}
	}

	/**
	 *
	 * @param studyName
	 * @param parameter
	 * @param value
	 */
	public void setStudyInputParameter(String studyName, String parameter, String value) {
		String args = buildArgumentStringFromArgs(studyName, parameter, value);
		String script = "setStudyParameter(" + args + ", true);";
		executeJavascript(script, toastCallback);
		addEvent(new Event("CHIQ_setStudyParameter").set("parameter", parameter).set("value", value));
	}

	/**
	 *
	 * @param studyName
	 * @param parameter
	 * @param value
	 */
	public void setStudyOutputParameter(String studyName, String parameter, String value) {
		String args = buildArgumentStringFromArgs(studyName, parameter, value);
		String script = "setStudyParameter(" + args + ", false);";
		executeJavascript(script, toastCallback);
		addEvent(new Event("CHIQ_setStudyParameter").set("parameter", parameter).set("value", value));
	}

	/**
	 *
	 * @param drawingName
	 * @return
	 */
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
			addEvent(new Event("CHIQ_getDrawingParameters").set("drawingName", drawingName));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
			addEvent(new Event("CHIQ_getDrawingParameters").set("drawingName", drawingName));
			return promise;
		}
	}

	/**
	 *
	 * @param json
	 */
	@JavascriptInterface
	public void layoutChange(JSONObject json) {
		for (OnLayoutChangedCallback callback : onLayoutChanged) {
			callback.execute(json);
		}
		addEvent(new Event("CHIQ_layoutChange").set("json", String.valueOf(json)));
	}

	/**
	 *
	 * @param json
	 */
	@JavascriptInterface
	public void drawingChange(JSONObject json) {
		for (OnDrawingChangedCallback callback : onDrawingChanged) {
			callback.execute(json);
		}
		addEvent(new Event("CHIQ_drawingChange").set("json", String.valueOf(json)));
	}

	/**
	 *
	 * @param symbol
	 * @param period
	 * @param interval
	 * @param start
	 * @param end
	 * @param meta
	 * @param id
	 */
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
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(id, data);
				}
			});
		}

		addEvent(new Event("CHIQ_pullInitialData").set("symbol", symbol).set("interval", period)
				.set("timeUnit", interval).set("start", String.valueOf(start)).set("end", String.valueOf(end))
				.set("meta", String.valueOf(meta)));
	}

	/**
	 *
	 * @param symbol
	 * @param period
	 * @param interval
	 * @param start
	 * @param meta
	 * @param callbackId
	 */
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
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(callbackId, data);
				}
			});
		}

		addEvent(new Event("CHIQ_pullUpdate").set("symbol", symbol).set("interval", period).set("timeUnit", interval)
				.set("start", String.valueOf(start)).set("meta", String.valueOf(meta)));
	}

	/**
	 *
	 * @param symbol
	 * @param period
	 * @param interval
	 * @param start
	 * @param end
	 * @param meta
	 * @param callbackId
	 */
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
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(callbackId, data);
				}
			});
		}
		addEvent(new Event("CHIQ_pullPagination").set("symbol", symbol).set("interval", period)
				.set("timeUnit", interval).set("start", String.valueOf(start)).set("end", String.valueOf(end))
				.set("meta", String.valueOf(meta)));
	}

	private void invokePullCallback(String callbackId, OHLCChart[] data) {
		String json = new Gson().toJson(data);
		executeJavascript("parseData('" + json + "', \"" + callbackId + "\");");
	}

	/**
	 * Execute function from chart engine object
	 * 
	 * @param functionName
	 *            javascript function to execute
	 * @param callback
	 *            java method to call when functionName is finished executing
	 * @param args
	 *            arguments to pass to javascript function
	 */
	public void invoke(final String functionName, final ValueCallback callback, final Object... args) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String script = CHART_IQ_JS_OBJECT + "." + functionName + "(" + buildArgumentStringFromArgs(args) + ")";
				// ValueCallback callback = getCallBackFromArgs(args);
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
	 * Execute function from specified object
	 * 
	 * @param jsObject
	 *            object to execute function from
	 * @param methodName
	 *            javascript function to execute
	 * @param args
	 *            arguments to pass to javascript function
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
	 * @param args
	 *            parameters that define what to change, must be put in order
	 *            (selector, property, value) ex:
	 *            changeChartStyle("stx_mountain_chart", "color", "blue");
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
	 * @param property
	 *            The property to change
	 * @param value
	 *            The value to change
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
			addEvent(new Event("CHIQ_getChartProperty").set("property", property));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
			addEvent(new Event("CHIQ_getChartProperty").set("property", property));
			return promise;
		}
	}

	/**
	 * Change a property value on the chart engine
	 * 
	 * @param property
	 *            The property to change
	 * @param value
	 *            The value to change
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
			addEvent(new Event("CHIQ_getEngineProperty").set("property", property));
			return promise;
		} else {
			final Promise<String> promise = new Promise<>();
			promises.add(promise);
			loadUrl("javascript:promises.setPromiseResult(" + promises.indexOf(promise) + ", script)");
			addEvent(new Event("CHIQ_getEngineProperty").set("property", property));
			return promise;
		}
	}

	private void executeJavascript(final String script) {
		executeJavascript(script, null);
	}

	private void executeJavascript(final String script, final ValueCallback<String> callback) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					evaluateJavascript(script, callback);
				} else {
					loadUrl("javascript:" + script);
				}
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

	public ChartIQ setShowDebugInfo(boolean showDebugInfo) {
		this.showDebugInfo = showDebugInfo;
		return this;
	}

	public enum DataMethod {
		PUSH, PULL
	}

	public enum QuoteFields {
		DATE("Date"), CLOSE("Close"), OPEN("Open"), HIGH("High"), LOW("Low"), VOLUME("Volume");

		private String quoteField;

		QuoteFields(String quoteField) {
			this.quoteField = quoteField;
		}

		public String value() {
			return quoteField;
		}
	}

	public void setTalkbackFields(HashMap<String, Boolean> talkbackFields) {
		this.talkbackFields = talkbackFields;
	}

	public interface CallbackStart {
		void onStart();
	}

	public interface SetCustomPropertyCallback {
		void onSetCustomProperty();
	}

	interface OnLayoutChangedCallback {
		void execute(JSONObject json);
	}

	interface OnDrawingChangedCallback {
		void execute(JSONObject json);
	}

	interface OnPullInitialDataCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Date end, Object meta);
	}

	interface OnPullUpdateCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Object meta);
	}

	interface OnPullPaginationCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Date end, Object meta);
	}
}
