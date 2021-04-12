package com.chartiq.demo.ui.chart.interval.list

import com.chartiq.sdk.model.TimeUnit

data class IntervalItem(
    val period: Int,
    val interval: Int,
    val timeUnit: TimeUnit,
    val isSelected: Boolean
)
