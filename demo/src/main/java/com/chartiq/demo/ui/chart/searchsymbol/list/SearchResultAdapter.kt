package com.chartiq.demo.ui.chart.searchsymbol.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSearchSymbolBinding

class SearchResultAdapter(private val listener: OnSearchResultClickListener) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    var list = listOf<SearchResultItem>()
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchResultViewHolder(
            ItemSearchSymbolBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            listener.onSearchItemClick(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

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
}
