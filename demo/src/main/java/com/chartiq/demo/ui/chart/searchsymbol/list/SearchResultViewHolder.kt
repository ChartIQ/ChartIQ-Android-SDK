package com.chartiq.demo.ui.chart.searchsymbol.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: SearchResultItem) {
        itemView.findViewById<TextView>(R.id.symbolTextView).text = item.symbol
        itemView.findViewById<TextView>(R.id.symbolFullNameTextView).text = item.fullName
        itemView.findViewById<TextView>(R.id.fundTextView).text = item.fund
    }
}
