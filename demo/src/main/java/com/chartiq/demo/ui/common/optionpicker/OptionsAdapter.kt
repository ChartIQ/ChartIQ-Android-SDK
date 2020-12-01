package com.chartiq.demo.ui.common.optionpicker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemRowSimpleValueBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener

class OptionsAdapter : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {

    var items = listOf<OptionItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: OnSelectItemListener<OptionItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return OptionViewHolder(ItemRowSimpleValueBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OptionViewHolder(private val binding: ItemRowSimpleValueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OptionItem) {
            binding.valueTextView.text = item.value
            binding.checkImageView.isVisible = item.isSelected
            binding.root.setOnClickListener {
                listener?.onSelected(item)
            }
        }
    }
}
