package com.chartiq.demo.ui.chart.searchsymbol.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

class SearchResultAdapter(private val listener: OnSearchResultClickListener) :
    RecyclerView.Adapter<SearchResultViewHolder>() {

    private var list = listOf<SearchResultItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search_symbol,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            listener.onSearchItemClick(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<SearchResultItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
