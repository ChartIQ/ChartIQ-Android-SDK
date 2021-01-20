package com.chartiq.demo.ui.chart.searchsymbol.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSearchSymbolBinding

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    var items = listOf<SearchResultItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnSearchResultClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchResultViewHolder(
            ItemSearchSymbolBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SearchResultViewHolder(private val binding: ItemSearchSymbolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchResultItem) {
            with(binding) {
                symbolTextView.text = item.symbol
                symbolFullNameTextView.text = item.fullName
                fundTextView.text = item.fund
                root.setOnClickListener {
                    listener?.onSearchItemClick(item)
                }
            }
        }
    }
}
