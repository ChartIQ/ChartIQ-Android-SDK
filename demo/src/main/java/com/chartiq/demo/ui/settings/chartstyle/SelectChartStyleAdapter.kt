package com.chartiq.demo.ui.settings.chartstyle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemChartStyleBinding

class SelectChartStyleAdapter : RecyclerView.Adapter<SelectChartStyleAdapter.ViewHolder>() {

    var items = listOf<ChartTypeItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: SelectChartStyleAdapterListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectChartStyleAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemChartStyleBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SelectChartStyleAdapter.ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemChartStyleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chartTypeItem: ChartTypeItem) {
            with(binding) {
                optionTextView.text = itemView.context.resources.getString(chartTypeItem.titleRes)
                checkImageView.isVisible = chartTypeItem.isSelected
                iconImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        chartTypeItem.iconRes
                    )
                )
                root.setOnClickListener {
                    listener?.onSelect(chartTypeItem)
                }
            }
        }
    }

    interface SelectChartStyleAdapterListener {
        fun onSelect(selectedValue: ChartTypeItem)
    }
}

