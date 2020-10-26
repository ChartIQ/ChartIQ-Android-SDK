package com.chartiq.demo.ui.chart.panel.color

import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelColorBinding
import com.chartiq.demo.ui.chart.panel.OnSelectedListener

class ColorViewHolder(private val binding: ItemPanelColorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ColorItem, listener: OnSelectedListener<ColorItem>) {
        binding.colorView.apply {
            (background.mutate() as GradientDrawable).setColor(item.color)
            foreground = if (item.isSelected) {
                ContextCompat.getDrawable(context, R.drawable.layer_color_selected_ok)
            } else {
                null
            }
            setOnClickListener {
                listener.onSelected(item)
            }
        }
    }
}
