package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding

class CustomIntervalViewHolder(private val binding: ItemIntervalChooseCustomBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(isSelected: Boolean) {
        with(binding) {
            if (isSelected) {
                checkImageView.visibility = View.VISIBLE
                forwardImageView.visibility = View.GONE
            } else {
                checkImageView.visibility = View.GONE
                forwardImageView.visibility = View.VISIBLE
            }
        }
    }
}
