package com.chartiq.demo.ui.chart.panel.layer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelLayerBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class LayersAdapter : RecyclerView.Adapter<LayersAdapter.LayerViewHolder>() {

    var items = listOf<LayerItem>()
    var listener: OnSelectItemListener<LayerItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LayerViewHolder(ItemPanelLayerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LayerViewHolder(private val binding: ItemPanelLayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LayerItem) {
            with(binding) {
                layerImageView.apply {
                    background = ContextCompat.getDrawable(context, item.iconRes)
                }
                layerTextView.setText(item.titleRes)
                root.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }
}
