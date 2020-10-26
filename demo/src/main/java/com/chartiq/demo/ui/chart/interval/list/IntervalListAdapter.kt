package com.chartiq.demo.ui.chart.interval.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding
import com.chartiq.demo.ui.chart.interval.list.viewholder.CustomIntervalViewHolder
import com.chartiq.demo.ui.chart.interval.list.viewholder.IntervalViewHolder

class IntervalListAdapter(
    private val intervalList: List<IntervalItem>,
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
            is CustomIntervalViewHolder -> {
                val isSelected = intervalList.firstOrNull { it.isSelected }
                holder.bind(isSelected == null, onIntervalClickListener)
            }
            is IntervalViewHolder -> {
                // [position - 1] because a custom interval item is added in the begging of the list
                val item = intervalList[position - 1]
                holder.bind(item, onIntervalClickListener)
            }
        }
    }

    override fun getItemCount(): Int = intervalList.size + 1

    companion object {
        private const val TYPE_CUSTOM = 0
        private const val TYPE_REGULAR = 1
    }
}
