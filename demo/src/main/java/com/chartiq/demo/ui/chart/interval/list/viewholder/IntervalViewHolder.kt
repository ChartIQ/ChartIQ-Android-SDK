package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.list.OnIntervalSelectListener

class IntervalViewHolder(private val binding: ItemIntervalBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: IntervalItem, onSelectInterval: OnIntervalSelectListener) {
        with(binding) {
            intervalTextView.text = getTimeUnitText(root.resources, item.duration, item.timeUnit)
            checkImageView.visibility = if (item.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
            root.setOnClickListener {
                onSelectInterval.onIntervalSelect(item)
            }
        }
    }
}
