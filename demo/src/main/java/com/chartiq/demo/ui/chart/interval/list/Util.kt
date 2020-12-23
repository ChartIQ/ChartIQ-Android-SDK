package com.chartiq.demo.ui.chart.interval.list

import android.content.res.Resources
import com.chartiq.demo.R
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

fun getTimeUnitText(resources: Resources, interval: Int, timeUnit: TimeUnit): String {
    with(resources) {
        return when (timeUnit) {
            TimeUnit.SECOND -> getString(R.string.interval_second, interval)
            TimeUnit.MINUTE -> getString(R.string.interval_minute, interval)
            TimeUnit.HOUR -> getString(R.string.interval_hour, interval)
            TimeUnit.DAY -> getString(R.string.interval_day, interval)
            TimeUnit.WEEK -> getString(R.string.interval_week, interval)
            TimeUnit.MONTH -> getString(R.string.interval_month, interval)
        }
    }
}
