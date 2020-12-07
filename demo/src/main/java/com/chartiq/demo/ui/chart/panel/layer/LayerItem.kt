package com.chartiq.demo.ui.chart.panel.layer

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.chartiq.sdk.model.ChartLayer

data class LayerItem(
    val layer: ChartLayer,
    @StringRes
    val titleRes: Int,
    @DrawableRes
    val iconRes: Int
)
