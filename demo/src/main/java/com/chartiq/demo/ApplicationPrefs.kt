package com.chartiq.demo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chartiq.demo.ui.chart.interval.model.Interval
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

interface ApplicationPrefs {

    fun getChartInterval(): Interval

    fun saveChartInterval(interval: Interval)

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
            val record = "%d  %s".format(interval.duration, interval.timeUnit.toString())
            prefs.edit(true) {
                putString(KEY_CHART_INTERVAL, record)
            }
        }
    }

    companion object {
        private const val PREF_FILE = "ApplicationPrefs"
        private const val KEY_CHART_INTERVAL = "chart.iq.demo.chart.interval"
        private const val DEFAULT_CHART_INTERVAL = "1 day"
    }
}
