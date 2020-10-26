package com.chartiq.demo.ui.chart.interval.list.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemIntervalChooseCustomBinding
import com.chartiq.demo.ui.chart.interval.list.OnIntervalClickListener

class CustomIntervalViewHolder(private val binding: ItemIntervalChooseCustomBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(isSelected: Boolean, onIntervalClickListener: OnIntervalClickListener) {
        with(binding) {
            if (isSelected) {
                checkImageView.visibility = View.VISIBLE
                forwardImageView.visibility = View.GONE
            } else {
                checkImageView.visibility = View.GONE
                forwardImageView.visibility = View.VISIBLE
            }
            root.setOnClickListener {
                onIntervalClickListener.onCustomIntervalClick()
            }
        }
    }
}
