package com.chartiq.demo.ui.chart.panel.line

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelLineTypeBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class LineViewHolder(private val binding: ItemPanelLineTypeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LineTypeItem, listener: OnSelectItemListener<LineTypeItem>) {
        binding.lineTypeImageView.apply {
            foreground = ContextCompat.getDrawable(context, item.iconRes)
            val colorInt = if (item.isSelected) {
                R.color.mountainMeadow
            } else {
                R.color.brightGrey
            }
            DrawableCompat.setTint(background.mutate(), ContextCompat.getColor(context, colorInt))
            setOnClickListener {
                listener.onSelected(item)
            }
        }
    }
}
