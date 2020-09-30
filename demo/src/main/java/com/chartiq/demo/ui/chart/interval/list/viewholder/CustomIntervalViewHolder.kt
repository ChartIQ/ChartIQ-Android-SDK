package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

class CustomIntervalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(isSelected: Boolean) {
        with(itemView) {
            if (isSelected) {
                findViewById<ImageView>(R.id.checkImageView).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.forwardImageView).visibility = View.GONE
            } else {
                findViewById<ImageView>(R.id.checkImageView).visibility = View.GONE
                findViewById<ImageView>(R.id.forwardImageView).visibility = View.VISIBLE
            }
        }
    }
}
