package com.chartiq.demo.ui.chart.panel.color

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelColorBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    var items = listOf<ColorItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnSelectItemListener<ColorItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ColorViewHolder(ItemPanelColorBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ColorViewHolder(private val binding: ItemPanelColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ColorItem) {
            binding.colorView.apply {
                (background.mutate() as GradientDrawable).setColor(item.color)
                foreground = if (item.isSelected) {
                    ContextCompat.getDrawable(context, R.drawable.layer_color_selected_ok)
                } else {
                    null
                }
                setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }
}
