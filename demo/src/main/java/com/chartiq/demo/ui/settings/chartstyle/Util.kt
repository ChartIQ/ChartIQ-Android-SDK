package com.chartiq.demo.ui.settings.chartstyle

import com.chartiq.demo.R
import com.chartiq.sdk.model.charttype.ChartAggregationType
import com.chartiq.sdk.model.charttype.ChartType

fun ChartAggregationType.toModel(): ChartTypeItem {
    return ChartTypeItem(
        titleRes = when (this) {
            ChartAggregationType.HEIKINASHI -> R.string.chart_type_heikinashi
            ChartAggregationType.KAGI -> R.string.chart_type_kagi
            ChartAggregationType.LINEBREAK -> R.string.chart_type_linebreak
            ChartAggregationType.RENKO -> R.string.chart_type_renko
            ChartAggregationType.RANGEBARS -> R.string.chart_type_rangebars
            ChartAggregationType.PANDF -> R.string.chart_type_pandf
        },
        name = name,
        iconRes = when (this) {
            ChartAggregationType.HEIKINASHI -> R.drawable.ic_chart_style_heikin_ashi
            ChartAggregationType.KAGI -> R.drawable.ic_chart_style_kagi
            ChartAggregationType.LINEBREAK -> R.drawable.ic_chart_style_line_break
            ChartAggregationType.RENKO -> R.drawable.ic_chart_style_renko
            ChartAggregationType.RANGEBARS -> R.drawable.ic_chart_style_range_bar
            ChartAggregationType.PANDF -> R.drawable.ic_chart_style_point_and_figure
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
