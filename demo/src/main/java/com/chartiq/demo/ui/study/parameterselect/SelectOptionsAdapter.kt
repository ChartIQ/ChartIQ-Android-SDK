package com.chartiq.demo.ui.study.parameterselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSelectOptionBinding

class SelectOptionsAdapter : RecyclerView.Adapter<SelectOptionsAdapter.ViewHolder>() {

    var items = mapOf<String, String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedValue: String? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: SelectOptionsAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectOptionsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemSelectOptionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items.entries.toList()[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemSelectOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(keyValue: Map.Entry<String, String>) {
            with(binding) {
                optionTextView.text = keyValue.value
                checkImageView.isVisible = selectedValue == keyValue.key
                root.setOnClickListener {
                    selectedValue = if (selectedValue != keyValue.key) {
                        keyValue.key
                    } else {
                        null
                    }
                    listener?.onSelect(keyValue)
                }
            }
        }
    }

    interface SelectOptionsAdapterListener {
        fun onSelect(keyValue: Map.Entry<String, String>)
    }
}
