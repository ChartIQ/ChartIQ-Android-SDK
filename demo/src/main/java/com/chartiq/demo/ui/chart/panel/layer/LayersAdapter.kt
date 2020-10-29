package com.chartiq.demo.ui.chart.panel.layer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelLayerBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class LayersAdapter(private val listener: OnSelectItemListener<LayerItem>) :
    RecyclerView.Adapter<LayerViewHolder>() {

    var items = listOf<LayerItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LayerViewHolder(ItemPanelLayerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size
}
