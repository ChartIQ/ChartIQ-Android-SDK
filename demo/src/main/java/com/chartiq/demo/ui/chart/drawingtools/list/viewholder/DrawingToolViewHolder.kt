package com.chartiq.demo.ui.chart.drawingtools.list.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolBinding
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem
import com.chartiq.sdk.model.drawingtool.DrawingTool

class DrawingToolViewHolder(
    private val binding: ItemDrawingToolBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DrawingToolItem, listener: OnDrawingToolClick) {
        with(binding) {
            iconImageView.background = ContextCompat.getDrawable(root.context, item.iconRes)
            toolNameTextView.text = root.resources.getString(item.nameRes)
            checkImageView.visibility = if (item.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
            when (item.tool) {
                DrawingTool.NO_TOOL,
                DrawingTool.MEASURE -> starIndicatorImageView.visibility = View.GONE
                else -> starIndicatorImageView.apply {
                    isChecked = item.isStarred
                    setOnCheckedChangeListener { button, isChecked ->
                        if (button.isPressed) {
                            listener.onFavoriteCheck(item)
                        }
                    }
                }
            }
            root.setOnClickListener {
                listener.onDrawingToolClick(item)
            }
        }
    }
}
