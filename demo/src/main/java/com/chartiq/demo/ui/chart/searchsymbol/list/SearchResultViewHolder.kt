package com.chartiq.demo.ui.chart.searchsymbol.list

import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSearchSymbolBinding

class SearchResultViewHolder(private val binding: ItemSearchSymbolBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SearchResultItem) {
        with(binding) {
            symbolTextView.text = item.symbol
            symbolFullNameTextView.text = item.fullName
            fundTextView.text = item.fund
        }
    }
}
