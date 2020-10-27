package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding
import com.chartiq.demo.ui.chart.interval.list.IntervalItem
import com.chartiq.demo.ui.chart.interval.list.OnIntervalSelectListener

class CustomIntervalViewHolder(private val binding: ItemIntervalChooseCustomBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: IntervalItem, onSelectInterval: OnIntervalSelectListener) {
        with(binding) {
            if (item.isSelected) {
                selectCustomTextView.text = getTimeUnitText(root.resources, item.duration, item.timeUnit)
                checkImageView.visibility = View.VISIBLE
                forwardImageView.visibility = View.GONE
            } else {
                checkImageView.visibility = View.GONE
                forwardImageView.visibility = View.VISIBLE
            }
            root.setOnClickListener {
                onSelectInterval.onCustomIntervalSelect()
            }
        }
    }
}
