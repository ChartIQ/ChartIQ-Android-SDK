package com.chartiq.demo.ui.settings.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemRowSimpleValueBinding

class SelectLanguageAdapter : RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder>() {

    var items = listOf<ChartIQLanguage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedValue: ChartIQLanguage? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: SelectLanguageListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectLanguageAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemRowSimpleValueBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SelectLanguageAdapter.ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemRowSimpleValueBinding   ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(iqLanguage: ChartIQLanguage) {
            with(binding) {
                valueTextView.text = iqLanguage.title
                checkImageView.isVisible = selectedValue == iqLanguage
                root.setOnClickListener {
                    selectedValue = if (selectedValue != iqLanguage) {
                        iqLanguage
                    } else {
                        null
                    }
                    listener?.onSelectLanguage(iqLanguage)
                }
            }
        }
    }

    interface SelectLanguageListener {
        fun onSelectLanguage(selectedValue: ChartIQLanguage)
    }
}

