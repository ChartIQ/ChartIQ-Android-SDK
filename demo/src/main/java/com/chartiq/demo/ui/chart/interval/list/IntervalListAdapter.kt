package com.chartiq.demo.ui.chart.interval.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalBinding
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding

class IntervalListAdapter : RecyclerView.Adapter<IntervalListAdapter.IntervalViewHolder>() {

    var items = listOf<IntervalItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onSelectIntervalListener: OnIntervalSelectListener? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_CUSTOM
        } else {
            TYPE_REGULAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntervalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CUSTOM -> IntervalViewHolder.CustomIntervalViewHolder(
                ItemIntervalChooseCustomBinding.inflate(inflater, parent, false)
            )
            TYPE_REGULAR -> IntervalViewHolder.RegularIntervalViewHolder(
                ItemIntervalBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: IntervalViewHolder, position: Int) {
        val item = items[position]
        when (getItemViewType(position)) {
            TYPE_CUSTOM ->
                (holder as IntervalViewHolder.CustomIntervalViewHolder)
                    .bind(item, onSelectIntervalListener)
            TYPE_REGULAR ->
                (holder as IntervalViewHolder.RegularIntervalViewHolder)
                    .bind(item, onSelectIntervalListener)
        }
    }

    override fun getItemCount(): Int = items.size

    sealed class IntervalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class RegularIntervalViewHolder(private val binding: ItemIntervalBinding) :
            IntervalViewHolder(binding.root) {

            fun bind(item: IntervalItem, onSelectInterval: OnIntervalSelectListener?) {
                with(binding) {
                    intervalTextView.text =
                        getTimeUnitText(root.resources, item.duration, item.timeUnit)
                    checkImageView.visibility = if (item.isSelected) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    root.setOnClickListener {
                        onSelectInterval?.onIntervalSelect(item)
                    }
                }
            }
        }

        class CustomIntervalViewHolder(private val binding: ItemIntervalChooseCustomBinding) :
            IntervalViewHolder(binding.root) {

            fun bind(item: IntervalItem, onSelectInterval: OnIntervalSelectListener?) {
                with(binding) {
                    if (item.isSelected) {
                        selectCustomTextView.text =
                            getTimeUnitText(root.resources, item.duration, item.timeUnit)
                        checkImageView.visibility = View.VISIBLE
                        forwardImageView.visibility = View.GONE
                    } else {
                        checkImageView.visibility = View.GONE
                        forwardImageView.visibility = View.VISIBLE
                    }
                    root.setOnClickListener {
                        onSelectInterval?.onCustomIntervalSelect()
                    }
                }
            }
        }
    }

    companion object {
        private const val TYPE_CUSTOM = 0
        private const val TYPE_REGULAR = 1
    }
}
