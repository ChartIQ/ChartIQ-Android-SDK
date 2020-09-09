package com.chartiq.demo.ui.chart.interval.list

import com.chartiq.demo.ui.chart.interval.model.Interval

interface OnIntervalClickListener {
    fun onCustomIntervalClick()

    fun onIntervalClick(interval: Interval)
}
