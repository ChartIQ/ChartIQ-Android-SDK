package com.chartiq.demo.ui.chart.panel

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.graphics.drawable.DrawableCompat
import com.chartiq.demo.R
import com.chartiq.sdk.model.drawingtool.LineType

private const val LINE_WIDTH_BOLD = 2
private const val LINE_WIDTH_EXTRA_BOLD = 3

private const val DEFAULT_COLOR_PICKER_COLOR = Color.BLACK
private const val COLOR_AUTO = "auto"

fun getLineTypeResource(lineType: LineType, lineWidth: Int): Int {
    return when (lineType) {
        LineType.SOLID ->
            when (lineWidth) {
                LINE_WIDTH_BOLD -> R.drawable.ic_line_type_solid_bold
                LINE_WIDTH_EXTRA_BOLD -> R.drawable.ic_line_type_solid_boldest
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

fun updatePickerColor(drawable: Drawable, colorParameter: String?) {
    val pickerDrawable = (drawable as LayerDrawable).findDrawableByLayerId(R.id.colorPicker)
    val color = if (colorParameter == null || colorParameter == COLOR_AUTO ) {
        DEFAULT_COLOR_PICKER_COLOR
    } else {
        Color.parseColor(colorParameter)
    }
    DrawableCompat.setTint(pickerDrawable.mutate(), color)
}
