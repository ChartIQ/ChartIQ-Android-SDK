package com.chartiq.demo.ui.chart.panel

import androidx.annotation.DrawableRes

data class PanelItem(
    val instrument: Instrument,
    @DrawableRes
    val iconRes: Int,
    val isSelected: Boolean = false
)
