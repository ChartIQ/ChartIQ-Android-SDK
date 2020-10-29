package com.chartiq.demo.ui.chart.panel

import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelInstrumentBinding
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem
import com.chartiq.sdk.model.LineType

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
                    // TODO: 28.10.20 Check the actual color
                    val drawable = (drawable as LayerDrawable)
                        .findDrawableByLayerId(R.id.colorPicker)
                    DrawableCompat.setTint(drawable.mutate(), Color.RED)
                }
                Instrument.FILL -> {
                    // TODO: 28.10.20 Check the actual color
                    val drawable = (drawable as LayerDrawable)
                        .findDrawableByLayerId(R.id.colorPicker)
                    DrawableCompat.setTint(drawable.mutate(), Color.GREEN)
                }
                Instrument.LINE_TYPE -> {
                    // TODO: 28.10.20 Check the actual line type
                    val lineType = LineType.DOTTED
                    val lineWidth = 2
                    val lineTypeDrawable = getLineType(lineType, lineWidth)
                    setImageResource(lineTypeDrawable)
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
}
