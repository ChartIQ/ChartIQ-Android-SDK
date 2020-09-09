package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

class CustomIntervalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(isSelected: Boolean) {
        if (isSelected) {
            itemView.findViewById<ImageView>(R.id.checkImageView).visibility = View.VISIBLE
        } else {
            itemView.findViewById<ImageView>(R.id.checkImageView).visibility = View.GONE
        }
    }
}
