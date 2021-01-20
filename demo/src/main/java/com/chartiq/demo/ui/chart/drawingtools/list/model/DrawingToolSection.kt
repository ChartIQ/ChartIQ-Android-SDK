package com.chartiq.demo.ui.chart.drawingtools.list.model

import androidx.annotation.StringRes
import com.chartiq.demo.R

enum class DrawingToolSection(@StringRes val stringRes: Int) {
    OTHER(R.string.drawing_tool_header_other_tools),
    MAIN(R.string.drawing_tool_header_main_tools)
}
