package com.chartiq.demo.ui.common.colorpicker

import android.graphics.Color

const val COLOR_AUTO = "auto"
private const val NO_SUCH_ITEM_IN_LIST_INDEX = -1
private const val COLOR_ALPHA_COMPONENT = "ff"
private const val HASH = "#"

fun findColorIndex(colors: List<ColorItem>, color: String?): Int? {
    if (color != null) {
        val selectedColor = if (color.isEmpty() || color == COLOR_AUTO) {
            colors.find { it.color == Color.BLACK }
        } else {
            colors.find { it.color == Color.parseColor(color) }
        }
        if (selectedColor != null) {
            val index = colors.indexOf(selectedColor)
            if (index != NO_SUCH_ITEM_IN_LIST_INDEX) {
                return index
            }
        }
    }
    return null
}

fun Int.toHexStringWithHash(): String =
    Integer.toHexString(this).replaceFirst(COLOR_ALPHA_COMPONENT, HASH)
