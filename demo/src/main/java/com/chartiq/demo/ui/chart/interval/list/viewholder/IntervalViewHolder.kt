package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.ui.chart.interval.model.TimeUnit

class IntervalViewHolder(private val binding: ItemIntervalBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(interval: Int, timeUnit: TimeUnit, isSelected: Boolean) {
        with(binding) {
            intervalTextView.text = getText(interval, timeUnit)
            checkImageView.visibility = if (isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun getText(interval: Int, timeUnit: TimeUnit): String {
        with(itemView.context.resources) {
            return when (timeUnit) {
                TimeUnit.SECOND -> getQuantityString(R.plurals.intervalDay, interval, interval)
                TimeUnit.MINUTE -> getQuantityString(R.plurals.intervalMinute, interval, interval)
                TimeUnit.HOUR -> getQuantityString(R.plurals.intervalHour, interval, interval)
                TimeUnit.DAY -> getQuantityString(R.plurals.intervalDay, interval, interval)
                TimeUnit.WEEK -> getQuantityString(R.plurals.intervalWeek, interval, interval)
                TimeUnit.MONTH -> getQuantityString(R.plurals.intervalMonth, interval, interval)
            }
        }
    }
}
