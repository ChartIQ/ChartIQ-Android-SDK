package com.chartiq.demo.ui.chart.panel

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelInstrumentBinding

class InstrumentViewHolder(private val binding: ItemPanelInstrumentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PanelItem, listener: OnSelectedListener<PanelItem>) {
        binding.iconImageButton.apply {
            setImageResource(item.iconRes)
            if (item.instrument == Instrument.DRAWING_TOOL) {
                val dimen = resources.getDimensionPixelSize(R.dimen.padding_extra_small)
                setPadding(dimen, dimen, dimen, dimen)
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
