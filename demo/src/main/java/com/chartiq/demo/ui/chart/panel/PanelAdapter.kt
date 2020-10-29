package com.chartiq.demo.ui.chart.panel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelInstrumentBinding
import com.chartiq.demo.ui.chart.panel.model.InstrumentItem

class PanelAdapter(private val listener: OnSelectItemListener<InstrumentItem>) :
    RecyclerView.Adapter<InstrumentViewHolder>() {

    var items = listOf<InstrumentItem>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrumentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return InstrumentViewHolder(ItemPanelInstrumentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: InstrumentViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size
}
