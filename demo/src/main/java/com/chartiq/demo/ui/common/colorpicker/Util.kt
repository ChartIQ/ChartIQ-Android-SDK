package com.chartiq.demo.ui.common.colorpicker

import android.content.res.Resources
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.chartiq.demo.R

const val COLOR_AUTO = "auto"
private const val NO_SUCH_ITEM_IN_LIST_INDEX = -1
private const val COLOR_ALPHA_COMPONENT = "ff"
private const val HASH = "#"

fun findColorIndex(colors: List<ColorItem>, @ColorInt color: Int): Int? {
    val selectedColor = colors.find { it.color == color }
    if (selectedColor != null) {
        val index = colors.indexOf(selectedColor)
        if (index != NO_SUCH_ITEM_IN_LIST_INDEX) {
            return index
        }
    }
    return null
}

@ColorInt
fun convertStringColorToInt(color: String): Int {
    return if (color.isEmpty() || color == COLOR_AUTO) {
        Color.BLACK
    } else {
        Color.parseColor(color)
    }
}

@ColorInt
fun convertStringColorToInt(color: String, resources: Resources): Int {
    return if (color.isEmpty() || color == COLOR_AUTO) {
        ResourcesCompat.getColor(resources, R.color.studyParameterAutoColor, null)
    } else {
        Color.parseColor(color)
    }
}

fun Int.toHexStringWithHash(): String =
        Integer.toHexString(this).replaceFirst(COLOR_ALPHA_COMPONENT, HASH)
