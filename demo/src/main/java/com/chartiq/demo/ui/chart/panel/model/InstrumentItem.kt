package com.chartiq.demo.ui.chart.panel.model

import androidx.annotation.DrawableRes

data class InstrumentItem(
    val instrument: Instrument,
    @DrawableRes
    val iconRes: Int,
    val isSelected: Boolean = false
)
