package com.chartiq.sdk

import android.content.Context
import android.view.View
import com.chartiq.sdk.model.*
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType
import com.chartiq.sdk.model.study.ChartIQStudy

interface ChartIQ : ChartIQDrawingTool, ChartIQStudy {
    /**
     * A ChartIQ [View] that represents a ChartIQ UI
     */
    val chartView: View

    /**
     * Sets a DataSource for ChartIQ. Note that it should be set before calling [ChartIQ.start]
     */
    fun setDataSource(dataSource: DataSource)

    /**
     * Starts a ChartIQ WebClient initialization
     * @param onStartCallback A callback to subscribe to for a successful WebClient initialization
     */
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

    /**
     * Sets an Aggregation type for charts
     * @param aggregationType A selected aggregation type
     */
    fun setAggregationType(aggregationType: AggregationChartType)

    /**
     * Sets an chart type for charts
     * @param chartType A selected chart type
     */
    fun setChartType(chartType: ChartType)

    /**
     * Gets a selected chart type
     * @param callback A callback to subscribe to to get a chart type
     */
    fun getChartType(callback: OnReturnCallback<ChartType?>)

    /**
     * Gets a selected aggregation chart type
     * @param callback A callback to subscribe to to get an aggregation chart type
     */
    fun getAggregationChartType(callback: OnReturnCallback<AggregationChartType?>)

    /**
     * Gets a selected chart scale
     * @param callback A callback to subscribe to to get a selected chart scale
     */
    fun getChartScale(callback: OnReturnCallback<ChartScale>)

    /**
     * Sets an chart scale for charts
     * @param scale A selected chart scale
     */
    fun setChartScale(scale: ChartScale)

    /**
     * Gets a selected chart Y axis invertion
     * @param callback A callback to subscribe to to get a selected Y-axis invertion.
     */
    fun getIsInvertYAxis(callback: OnReturnCallback<Boolean>)

    /**
     * Setting to true causes the y-axis and all linked drawings, series and studies to display inverted (flipped) from
     * its previous state
     * @param inverted  A selected invertion value. if true, Y axis is inverted
     */
    fun setIsInvertYAxis(inverted: Boolean)

    /**
     * Gets a selected extended-hours visualization
     * @param callback A callback to subscribe to to get a  extended hours flag value
     * If true, a chart uses extended hours
     */
    fun getIsExtendedHours(callback: OnReturnCallback<Boolean>)

    /**
     * Sets to turn on/off the extended-hours visualization.
     * @param extended  A selected boolean extended hours value. if true, extended hours are applied
     */
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
