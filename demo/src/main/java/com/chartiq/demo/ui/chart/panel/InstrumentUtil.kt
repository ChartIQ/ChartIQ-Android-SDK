package com.chartiq.demo.ui.chart.panel

import com.chartiq.demo.R
import com.chartiq.sdk.model.LineType

const val LINE_WIDTH_BOLD = 2
const val LINE_WIDTH_BOLDEST = 3

fun getLineType(lineType: LineType, lineWidth: Int): Int {
    return when (lineType) {
        LineType.SOLID ->
            when (lineWidth) {
                LINE_WIDTH_BOLD -> R.drawable.ic_line_type_solid_bold
                LINE_WIDTH_BOLDEST -> R.drawable.ic_line_type_solid_boldest
                else -> R.drawable.ic_line_type_solid
            }
        LineType.DOTTED ->
            when (lineWidth) {
                LINE_WIDTH_BOLD -> R.drawable.ic_line_type_dotted_bold
                else -> R.drawable.ic_line_type_dotted
            }
        LineType.DASHED -> when (lineWidth) {
            LINE_WIDTH_BOLD -> R.drawable.ic_line_type_dash_bold
            else -> R.drawable.ic_line_type_dash
        }
    }
}
