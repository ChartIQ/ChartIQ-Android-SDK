package com.chartiq.demo.ui.settings.chartstyle

import com.chartiq.demo.R
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType

fun AggregationChartType.toModel(): ChartTypeItem {
    return ChartTypeItem(
        title = value,
        name = name,
        iconRes = when (this) {
            AggregationChartType.HEIKINASHI -> R.drawable.ic_chart_style_heikin_ashi
            AggregationChartType.KAGI -> R.drawable.ic_chart_style_kagi
            AggregationChartType.LINEBREAK -> R.drawable.ic_chart_style_line_break
            AggregationChartType.RENKO -> R.drawable.ic_chart_style_renko
            AggregationChartType.RANGEBARS -> R.drawable.ic_chart_style_range_bar
            AggregationChartType.PANDF -> R.drawable.ic_chart_style_point_and_figure
        }
    )
}

fun ChartType.toModel(): ChartTypeItem {
    return ChartTypeItem(
        title = value,
        name = name,
        iconRes = when (this) {
            ChartType.CANDLE -> R.drawable.ic_chart_style_candles
            ChartType.BAR -> R.drawable.ic_chart_style_bar
            ChartType.COLORED_BAR -> R.drawable.ic_chart_style_colored_bar
            ChartType.LINE -> R.drawable.ic_chart_style_line
            ChartType.VERTEX_LINE -> R.drawable.ic_chart_style_vertex_line
            ChartType.STEP -> R.drawable.ic_chart_style_step
            ChartType.MOUNTAIN -> R.drawable.ic_chart_style_mountain
            ChartType.BASELINE -> R.drawable.ic_chart_style_baseline
            ChartType.HOLLOW_CANDLE -> R.drawable.ic_chart_style_hollow_candle
            ChartType.VOLUME_CANDLE -> R.drawable.ic_chart_style_volume_candle
            ChartType.COLORED_HLC -> R.drawable.ic_chart_style_colored_hlc_bar
            ChartType.SCATTERPLOT -> R.drawable.ic_chart_style_scatterplot
            ChartType.HISTOGRAM -> R.drawable.ic_chart_style_histogram
        }
    )
}
