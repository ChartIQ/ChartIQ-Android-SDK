package com.chartiq.demo.ui.chart.panel.layer

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelLayerBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class LayerViewHolder(private val binding: ItemPanelLayerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LayerItem, listener: OnSelectItemListener<LayerItem>) {
        with(binding) {
            layerImageView.apply {
                background = ContextCompat.getDrawable(context, item.iconRes)
            }
            layerTextView.setText(item.titleRes)
            root.setOnClickListener {
                listener.onSelected(item)
            }
        }
    }
}
