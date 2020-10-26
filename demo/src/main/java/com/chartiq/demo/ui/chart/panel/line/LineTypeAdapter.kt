package com.chartiq.demo.ui.chart.panel.line

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemPanelLineTypeBinding
import com.chartiq.demo.ui.chart.panel.OnSelectedListener

class LineTypeAdapter : RecyclerView.Adapter<LineViewHolder>(),
    OnSelectedListener<LineTypeItem> {

    var items = listOf<LineTypeItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LineViewHolder(ItemPanelLineTypeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        holder.bind(items[position], this)
    }

    override fun getItemCount(): Int = items.size

    override fun onSelected(item: LineTypeItem) {
        items = items.map {
            it.copy(isSelected = it == item)
        }
        notifyDataSetChanged()
    }
}
