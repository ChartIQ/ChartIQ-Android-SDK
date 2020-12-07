package com.chartiq.demo.ui.common.linepicker

import com.chartiq.sdk.model.drawingtool.LineType

private const val NO_SUCH_ITEM_IN_LIST_INDEX = -1

fun findLineIndex(
    lines: List<LineItem>,
    lineType: LineType?,
    lineWidth: Int?
): Int? {
    val selectedLine = lines.find {
        it.lineType == lineType && it.lineWidth == lineWidth
    }
    if (selectedLine != null) {
        val index = lines.indexOf(selectedLine)
        if (index != NO_SUCH_ITEM_IN_LIST_INDEX) {
            return index
        }
    }
    return null
}