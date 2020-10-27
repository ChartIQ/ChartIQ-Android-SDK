package com.chartiq.demo.ui.chart.interval.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding
import com.chartiq.demo.ui.chart.interval.list.viewholder.CustomIntervalViewHolder
import com.chartiq.demo.ui.chart.interval.list.viewholder.IntervalViewHolder

class IntervalListAdapter(private val onSelectInterval: OnIntervalSelectListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = listOf<IntervalItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        val item = items[position]
        when (getItemViewType(position)) {
            TYPE_CUSTOM ->
                (holder as CustomIntervalViewHolder).bind(item, onSelectInterval)
            TYPE_REGULAR ->
                (holder as IntervalViewHolder).bind(item, onSelectInterval)

        }
    }

    override fun getItemCount(): Int = items.size

    companion object {
        private const val TYPE_CUSTOM = 0
        private const val TYPE_REGULAR = 1
    }
}
