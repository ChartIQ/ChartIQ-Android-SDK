package com.chartiq.sdk

import android.content.Context
import android.view.View
import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.study.ChartIQStudy

interface ChartIQ : ChartIQDrawingTool, ChartIQStudy {

    val chartView: View

    /**
     * Sets a DataSource for ChartIQ. Note that it should be set before calling [ChartIQ.start]
     */
    fun setDataSource(dataSource: DataSource)

    fun start(onStartCallback: OnStartCallback)

    /**
     * Sets a symbol to the chart
     * @param symbol A symbol to be set
     */
    fun setSymbol(symbol: String)

    /**
     * Sets data method and symbol to the chart
     * @param method A data method to be set
     * @param symbol A symbol to be set
     */
    fun setDataMethod(method: DataMethod, symbol: String)

    /**
     * Enables crosshairs
     */
    fun enableCrosshairs()

    /**
     * Disables crosshairs
     */
    fun disableCrosshairs()

    /**
     * Sets periodicity to the chart
     * @param period A number of elements from masterData to roll-up together into one data point on the chart.
     * @param interval An interval is a numeric portion of the time unit.
     * @param timeUnit A particular time interval that represents a time unit. If not set, will default to "minute". *`hour` is NOT a valid timeUnit. Use `timeUnit:"minute", interval:60` instead.
     */
    fun setPeriodicity(period: Int, interval: String, timeUnit: TimeUnit)

    fun setAggregationType(aggregationType: AggregationChartType)

    fun setChartType(chartType: ChartType)

    fun getChartType(callback: OnReturnCallback<ChartType?>)

    fun getAggregationChartType(callback: OnReturnCallback<AggregationChartType?>)

    fun getChartScale(callback: OnReturnCallback<ChartScale>)

    fun setChartScale(scale: ChartScale)

    fun getIsInvertYAxis(callback: OnReturnCallback<Boolean>)

    fun setIsInvertYAxis(inverted: Boolean)

    fun getIsExtendedHours(callback: OnReturnCallback<Boolean>)

    fun setExtendedHours(extended: Boolean)

    /**
     * Get HUD details
     * @param callback A callback to subscribe to to receive [CrosshairHUD]
     */
    fun getHUDDetails(callback: OnReturnCallback<CrosshairHUD>)

    /**
     * Returns a map of translations for a given language
     * @param languageCode A selected language code in the  ISO 639-1 format
     * @param callback A callback to subscribe on to receive translations
     */
    fun getTranslations(languageCode: String, callback: OnReturnCallback<Map<String, String>>)

    /**
     * Sets a language that should be used within the app
     * @param languageCode A selected language code in the  ISO 639-1 format
     */
    fun setLanguage(languageCode: String)

    /**
     * Adds a measure listener
     * @param measureCallback A [MeasureCallback] to be added
     */
    fun addMeasureListener(measureCallback: MeasureCallback)

    /**
     * Sets a theme to the chart
     * @param theme A theme [ChartTheme] to be applied to the chart
     */
    fun setTheme(theme: ChartTheme)

    fun getActiveSeries(callback: OnReturnCallback<List<Series>>)

    fun addSeries(series: Series, isComparison: Boolean)

    fun removeSeries(symbolName: String)

    fun setSeriesParameter(symbolName: String, field: String, value: String)

    companion object {
        fun getInstance(url: String, context: Context): ChartIQ {
            return ChartIQHandler(url, context)
        }
    }
}
