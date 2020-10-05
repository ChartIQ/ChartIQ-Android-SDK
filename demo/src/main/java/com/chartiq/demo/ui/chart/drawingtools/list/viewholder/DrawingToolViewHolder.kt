package com.chartiq.demo.ui.chart.drawingtools.list.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolBinding
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem

class DrawingToolViewHolder(
    private val binding: ItemDrawingToolBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DrawingToolItem, listener: OnDrawingToolClick, position: Int) {
        with(binding) {
            iconImageView.background = ContextCompat.getDrawable(root.context, item.iconRes)
            toolNameTextView.text = root.resources.getString(item.nameRes)
            checkImageView.visibility = if (item.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
            starIndicatorImageView.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    listener.onFavoriteChecked(position, isChecked)
                }
                isChecked = item.isStarred
            }
            binding.root.setOnClickListener {
                listener.onDrawingToolClick(position)
            }
        }
    }
}
