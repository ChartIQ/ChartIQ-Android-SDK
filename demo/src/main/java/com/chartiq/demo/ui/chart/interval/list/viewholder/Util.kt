package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.content.res.Resources
import com.chartiq.demo.R
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

fun getTimeUnitText(resources: Resources, interval: Int, timeUnit: TimeUnit): String {
    with(resources) {
        return when (timeUnit) {
            TimeUnit.SECOND -> getQuantityString(R.plurals.intervalSecond, interval, interval)
            TimeUnit.MINUTE -> getQuantityString(R.plurals.intervalMinute, interval, interval)
            TimeUnit.HOUR -> getQuantityString(R.plurals.intervalHour, interval, interval)
            TimeUnit.DAY -> getQuantityString(R.plurals.intervalDay, interval, interval)
            TimeUnit.WEEK -> getQuantityString(R.plurals.intervalWeek, interval, interval)
            TimeUnit.MONTH -> getQuantityString(R.plurals.intervalMonth, interval, interval)
        }
    }
}
