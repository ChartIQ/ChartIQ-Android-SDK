package com.chartiq.demo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.sdk.model.DrawingTool
import java.util.*
import com.chartiq.sdk.model.drawingtool.DrawingTool

interface ApplicationPrefs {

    fun getChartInterval(): Interval

    fun saveChartInterval(interval: Interval)

    fun getChartSymbol(): Symbol

    fun saveChartSymbol(symbol: Symbol)

    fun saveDrawingTool(tool: DrawingTool)

    fun getDrawingTool(): DrawingTool

    fun saveFavoriteDrawingTools(drawingToolsSet: Set<DrawingTool>)

    fun getFavoriteDrawingTools(): Set<DrawingTool>

    fun clearSession()

    fun getApplicationId(): String


    class Default(context: Context) : ApplicationPrefs {
        private val prefs: SharedPreferences by lazy {
            context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        }

        override fun getChartInterval(): Interval {
            val result = prefs.getString(KEY_CHART_INTERVAL, DEFAULT_CHART_INTERVAL)!!
            val intervalValue = result.filter { it.isDigit() }
            val timeUnit = result.filter { it.isLetter() }.toUpperCase()
            return Interval(intervalValue.toInt(), TimeUnit.valueOf(timeUnit))
        }

        override fun saveChartInterval(interval: Interval) {
            val record = FORMATTING_INTERVAL.format(interval.duration, interval.timeUnit.toString())
            prefs.edit(true) {
                putString(KEY_CHART_INTERVAL, record)
            }
        }

        override fun getChartSymbol(): Symbol =
            Symbol(prefs.getString(KEY_CHART_SYMBOL, DEFAULT_CHART_SYMBOL)!!)

        override fun saveChartSymbol(symbol: Symbol) = prefs.edit(true) {
            putString(KEY_CHART_SYMBOL, symbol.value)
        }

        override fun saveDrawingTool(tool: DrawingTool) = prefs.edit(true) {
            putString(KEY_DRAWING_TOOL, tool.toString())
        }

        override fun getDrawingTool(): DrawingTool =
            DrawingTool.valueOf(prefs.getString(KEY_DRAWING_TOOL, DrawingTool.NO_TOOL.toString())!!)

        override fun saveFavoriteDrawingTools(drawingToolsSet: Set<DrawingTool>) = prefs.edit {
            val set = drawingToolsSet
                .map { it.toString() }
                .toHashSet()
            putStringSet(KEY_DRAWING_TOOL_FAVORITE, set)
        }

        override fun getFavoriteDrawingTools(): Set<DrawingTool> {
            return prefs.getStringSet(KEY_DRAWING_TOOL_FAVORITE, setOf())!!
                .map { DrawingTool.valueOf(it.toUpperCase()) }
                .toHashSet()
        }

        override fun clearSession() {
            prefs.edit(true) {
                putString(KEY_DRAWING_TOOL, DrawingTool.NO_TOOL.toString())
            }
        }

        override fun getApplicationId(): String {
            val storedId = prefs.getString(KEY_APPLICATION_ID, null)
            return if (storedId == null) {
                val newId = UUID.randomUUID().toString()
                prefs.edit(true) {
                    putString(KEY_APPLICATION_ID, newId)
                }
                newId
            } else storedId
        }
    }

    companion object {
        private const val PREF_FILE = "ApplicationPrefs"

        private const val KEY_CHART_INTERVAL = "chart.iq.demo.chart.interval"
        private const val KEY_CHART_SYMBOL = "chart.iq.demo.chart.symbol"
        private const val KEY_DRAWING_TOOL_FAVORITE = "chart.iq.demo.chart.drawingtool.favorites"
        private const val KEY_DRAWING_TOOL = "chart.iq.demo.chart.drawingtool.tool"
        private const val KEY_APPLICATION_ID = "chart.iq.demo.chart.applicationid"

        private const val DEFAULT_CHART_INTERVAL = "1 day"
        private const val DEFAULT_CHART_SYMBOL = "AAPL"

        private const val FORMATTING_INTERVAL = "%d %s"
    }
}
