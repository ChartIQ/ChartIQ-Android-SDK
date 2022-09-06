package com.chartiq.demo.ui.chart.comparison

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemCompareBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.common.colorpicker.convertStringColorToInt
import com.chartiq.sdk.model.Series

class CompareAdapter : RecyclerView.Adapter<CompareAdapter.CompareViewHolder>() {

    var items = listOf<Series>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: OnSelectItemListener<Series>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompareViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CompareViewHolder(ItemCompareBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CompareViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CompareViewHolder(private val binding: ItemCompareBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Series) {
            with(binding) {
                titleTextView.text = item.symbolName
                (colorImageView.background as GradientDrawable).setColor(
                    convertStringColorToInt(
                        item.color,
                        itemView.resources
                    )
                )
                colorImageView.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }
}
