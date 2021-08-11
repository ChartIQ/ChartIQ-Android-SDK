package com.chartiq.demo.ui.chart.panel

import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelInstrumentBinding
import com.chartiq.demo.network.model.PanelDrawingToolParameter
import com.chartiq.demo.ui.chart.panel.model.Instrument
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem

class InstrumentPanelAdapter : RecyclerView.Adapter<InstrumentPanelAdapter.InstrumentViewHolder>() {

    var parameter: PanelDrawingToolParameter? = null

    var items = listOf<InstrumentItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: OnSelectItemListener<InstrumentItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InstrumentViewHolder(ItemPanelInstrumentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class InstrumentViewHolder(private val binding: ItemPanelInstrumentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InstrumentItem) {
            binding.iconImageButton.apply {
                setImageResource(item.iconRes)
                when (item.instrument) {
                    Instrument.DRAWING_TOOL -> {
                        val dimen = resources.getDimensionPixelSize(R.dimen.padding_extra_small)
                        setPadding(dimen, dimen, dimen, dimen)
                    }
                    Instrument.COLOR -> {
                        parameter?.color?.let {
                            (drawable as LayerDrawable).updatePickerColor(it, R.id.colorPicker, resources)
                        }
                    }
                    Instrument.FILL -> {
                        parameter?.fillColor?.let {
                            (drawable as LayerDrawable).updatePickerColor(it, R.id.colorPicker, resources)
                        }
                    }
                    Instrument.LINE_TYPE -> {
                        parameter?.lineType?.let {
                            val lineType = it.getLineTypeResource(parameter?.lineWidth ?: 0)
                            setImageResource(lineType)
                        }
                    }
                }
                setOnClickListener {
                    listener?.onSelected(item)
                }
            }
            binding.panelInstrumentLayout.apply {
                val colorInt = if (item.isSelected) {
                    R.color.mountainMeadow
                } else {
                    android.R.color.transparent
                }
                DrawableCompat.setTint(
                    background.mutate(),
                    ContextCompat.getColor(context, colorInt)
                )
            }
        }
    }
}
