package com.chartiq.demo.ui.chart.interval.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding
import com.chartiq.demo.ui.chart.interval.list.viewholder.CustomIntervalViewHolder
import com.chartiq.demo.ui.chart.interval.list.viewholder.IntervalViewHolder

class IntervalListAdapter(
    private val intervalList: List<IntervalProps>,
    private val onIntervalClickListener: OnIntervalClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_CUSTOM
        } else {
            TYPE_REGULAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CUSTOM -> CustomIntervalViewHolder(
                ItemIntervalChooseCustomBinding.inflate(inflater, parent, false)
            )
            TYPE_REGULAR -> IntervalViewHolder(
                ItemIntervalBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is IntervalViewHolder -> {
                // [position - 1] instead of [position] because of an added item in getItemCount()
                // so the list has custom interval view holder as a first item no matter how big the
                // original list of intervals is
                val item = intervalList[position - 1]
                holder.bind(item.duration, item.timeUnit, item.isSelected)
                holder.itemView.setOnClickListener {
                    val selectedItem = intervalList.find { it.isSelected }
                    if (selectedItem != null) {
                        selectedItem.isSelected = false
                        notifyItemChanged(intervalList.indexOf(selectedItem) + 1)
                    } else {
                        notifyItemChanged(0)
                    }
                    item.isSelected = true
                    holder.bind(item.duration, item.timeUnit, item.isSelected)
                    onIntervalClickListener.onIntervalClick(item)
                }
            }
            is CustomIntervalViewHolder -> {
                val isSelected = intervalList.firstOrNull { it.isSelected }
                holder.bind(isSelected == null)
                holder.itemView.setOnClickListener {
                    onIntervalClickListener.onCustomIntervalClick()
                }
            }
        }
    }

    override fun getItemCount(): Int = intervalList.size + 1

    companion object {
        private const val TYPE_CUSTOM = 0
        private const val TYPE_REGULAR = 1
    }
}
