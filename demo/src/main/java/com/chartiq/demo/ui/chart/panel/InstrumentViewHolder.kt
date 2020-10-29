package com.chartiq.demo.ui.chart.panel

import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelInstrumentBinding
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem

class InstrumentViewHolder(private val binding: ItemPanelInstrumentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: InstrumentItem, listener: OnSelectItemListener<InstrumentItem>) {
        binding.iconImageButton.apply {
            setImageResource(item.iconRes)
            when (item.instrument) {
                Instrument.DRAWING_TOOL -> {
                    val dimen = resources.getDimensionPixelSize(R.dimen.padding_extra_small)
                    setPadding(dimen, dimen, dimen, dimen)
                }
                Instrument.COLOR -> {
                    val drawable = (drawable as LayerDrawable)
                        .findDrawableByLayerId(R.id.colorPicker)

//                    getDrawingToolParameters()?.let {
//                        val color = if (it.color == COLOR_AUTO) {
//                            Color.BLACK
//                        } else {
//                            Color.parseColor(it.color)
//                        }
//                        DrawableCompat.setTint(drawable.mutate(), color)
//                    }
                }
                Instrument.FILL -> {
                    val drawable = (drawable as LayerDrawable)
                        .findDrawableByLayerId(R.id.colorPicker)
//                    getDrawingToolParameters()?.let {
//                        val color = if (it.fillColor == COLOR_AUTO) {
//                            Color.BLACK
//                        } else {
//                            Color.parseColor(it.fillColor)
//                        }
//                        DrawableCompat.setTint(drawable.mutate(), color)
//                    }
                }
                Instrument.LINE_TYPE -> {
//                    getDrawingToolParameters()?.let {
//                        val lineType = it.pattern
//                        val lineWidth = it.lineWidth
//                        val lineTypeDrawable = getLineType(lineType, lineWidth)
//                        setImageResource(lineTypeDrawable)
//                    }
                }
            }
            setOnClickListener {
                listener.onSelected(item)
            }
        }
        binding.panelInstrumentLayout.apply {
            val colorInt = if (item.isSelected) {
                R.color.mountainMeadow
            } else {
                android.R.color.transparent
            }
            DrawableCompat.setTint(background.mutate(), ContextCompat.getColor(context, colorInt))
        }
    }

    companion object {
        private const val COLOR_AUTO = "auto"
    }
}
