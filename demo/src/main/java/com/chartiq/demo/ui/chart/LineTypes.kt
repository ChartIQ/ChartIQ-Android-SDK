package com.chartiq.demo.ui.chart

import androidx.annotation.DrawableRes
import com.chartiq.demo.R
import com.chartiq.sdk.model.drawingtool.LineType

enum class LineTypes(
    val lineType: LineType,
    val lineWidth: Int,
    @DrawableRes
    val iconRes: Int,
) {
    SOLID(LineType.SOLID, 1, R.drawable.ic_line_type_solid),
    SOLID_BOLD(LineType.SOLID, 2, R.drawable.ic_line_type_solid_bold),
    SOLID_BOLDEST(LineType.SOLID, 3, R.drawable.ic_line_type_solid_boldest),
    DOTTED(LineType.DOTTED, 1, R.drawable.ic_line_type_dotted),
    DOTTED_BOLD(LineType.DOTTED, 2, R.drawable.ic_line_type_dotted_bold),
    DASH(LineType.DASHED, 1, R.drawable.ic_line_type_dash),
    DASH_BOLD(LineType.DASHED, 2, R.drawable.ic_line_type_dash_bold)
}