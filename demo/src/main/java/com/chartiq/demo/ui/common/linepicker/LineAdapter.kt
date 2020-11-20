package com.chartiq.demo.ui.common.linepicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemPanelLineTypeBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class LineAdapter : RecyclerView.Adapter<LineAdapter.LineViewHolder>() {

    var items = listOf<LineItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnSelectItemListener<LineItem>? = null
    var viewHolderConfiguration: LineViewHolderConfiguration? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LineViewHolder(ItemPanelLineTypeBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LineViewHolder(private val binding: ItemPanelLineTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LineItem) {
            binding.lineTypeImageView.apply {
                viewHolderConfiguration?.let { config ->
                    minimumWidth = config.minWidth ?: minimumWidth
                    minimumHeight = config.minHeight ?: minimumHeight
                }
                foreground = ContextCompat.getDrawable(context, item.iconRes)
                val colorInt = if (item.isSelected) {
                    R.color.mountainMeadow
                } else {
                    R.color.brightGrey
                }
                DrawableCompat.setTint(
                    background.mutate(),
                    ContextCompat.getColor(context, colorInt)
                )
                setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }
}
