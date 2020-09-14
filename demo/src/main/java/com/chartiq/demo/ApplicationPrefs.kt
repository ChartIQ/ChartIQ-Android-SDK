package com.chartiq.demo

import android.content.Context
import android.content.SharedPreferences
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit
import com.chartiq.demo.ui.chart.searchsymbol.Symbol

interface ApplicationPrefs {

    fun getChartInterval(): Interval

    fun saveChartInterval(interval: Interval)

    fun getChartSymbol(): Symbol

    fun saveChartSymbol(symbol: Symbol)

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
            val record = FORMATTING_INTERVAL.format(interval.value, interval.timeUnit.toString())
            prefs
                .edit()
                .putString(KEY_CHART_INTERVAL, record)
                .apply()
        }

        override fun getChartSymbol(): Symbol =
            Symbol(prefs.getString(KEY_CHART_SYMBOL, DEFAULT_CHART_SYMBOL)!!)

        override fun saveChartSymbol(symbol: Symbol) = prefs
            .edit()
            .putString(KEY_CHART_SYMBOL, symbol.value)
            .apply()
    }

    companion object {
        private const val PREF_FILE = "ApplicationPrefs"

        private const val KEY_CHART_INTERVAL = "chart.iq.demo.chart.interval"
        private const val KEY_CHART_SYMBOL = "chart.iq.demo.chart.symbol"

        private const val DEFAULT_CHART_INTERVAL = "1 day"
        private const val DEFAULT_CHART_SYMBOL = "AAPL"

        private const val FORMATTING_INTERVAL = "%d  %s"
    }
}
