package com.chartiq.demo.ui.chart.interval.model

import com.chartiq.sdk.model.TimeUnit

data class Interval(
    val duration: Int,
    private val timeUnit: TimeUnit
) {
    fun getPeriod(): Int {
        return if (timeUnit == TimeUnit.HOUR) {
            duration * 2
        } else {
            1
        }
    }

    fun getInterval(): String {
        val interval = if (timeUnit == TimeUnit.HOUR) {
            30
        } else {
            duration
        }
        return "$interval"
    }

    fun getTimeUnit(): TimeUnit {
        return if (timeUnit == TimeUnit.HOUR) {
            TimeUnit.MINUTE
        } else {
            timeUnit
        }
    }
}
