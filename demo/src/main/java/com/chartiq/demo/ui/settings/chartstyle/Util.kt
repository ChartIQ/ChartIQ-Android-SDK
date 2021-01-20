package com.chartiq.demo.ui.settings.chartstyle

import com.chartiq.demo.R
import com.chartiq.sdk.model.charttype.AggregationChartType
import com.chartiq.sdk.model.charttype.ChartType

fun AggregationChartType.toModel(): ChartTypeItem {
    return ChartTypeItem(
        titleRes = when (this) {
            AggregationChartType.HEIKINASHI -> R.string.chart_type_heikinashi
            AggregationChartType.KAGI -> R.string.chart_type_kagi
            AggregationChartType.LINEBREAK -> R.string.chart_type_linebreak
            AggregationChartType.RENKO -> R.string.chart_type_renko
            AggregationChartType.RANGEBARS -> R.string.chart_type_rangebars
            AggregationChartType.PANDF -> R.string.chart_type_pandf
        },
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
        titleRes = when (this) {
            ChartType.CANDLE -> R.string.chart_type_candle
            ChartType.BAR -> R.string.chart_type_bar
            ChartType.COLORED_BAR -> R.string.chart_type_colored_bar
            ChartType.LINE -> R.string.chart_type_line
            ChartType.VERTEX_LINE -> R.string.chart_type_vertex_line
            ChartType.STEP -> R.string.chart_type_step
            ChartType.MOUNTAIN -> R.string.chart_type_mountain
            ChartType.BASELINE -> R.string.chart_type_baseline
            ChartType.HOLLOW_CANDLE -> R.string.chart_type_hollow_candle
            ChartType.VOLUME_CANDLE -> R.string.chart_type_volume_candle
            ChartType.COLORED_HLC -> R.string.chart_type_colored_hlc
            ChartType.SCATTERPLOT -> R.string.chart_type_scatterplot
            ChartType.HISTOGRAM -> R.string.chart_type_histogram
        },
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
