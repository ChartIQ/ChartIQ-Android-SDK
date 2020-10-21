package com.chartiq.demo.ui.chart.drawingtools.list.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolHeaderBinding
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolSection

class DrawingToolHeaderViewHolder(private val binding: ItemDrawingToolHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(section: DrawingToolSection) {
        binding.headerTextView.text = binding.root.resources.getString(section.stringRes)
    }
}
