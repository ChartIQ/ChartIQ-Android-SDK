package com.chartiq.demo.ui.chart.panel.color

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelColorBinding
import com.chartiq.demo.ui.chart.panel.OnSelectedListener

class ColorsAdapter : RecyclerView.Adapter<ColorViewHolder>(),
    OnSelectedListener<ColorItem> {

    var items = listOf<ColorItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(ItemPanelColorBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(items[position], this)
    }

    override fun getItemCount(): Int = items.size

    override fun onSelected(item: ColorItem) {
        items = items.map {
            it.copy(isSelected = it == item)
        }
        notifyDataSetChanged()
    }
}
