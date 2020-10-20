package com.chartiq.demo.ui.chart.drawingtools.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolBinding
import com.chartiq.demo.databinding.ItemDrawingToolHeaderBinding
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.DrawingToolHeaderViewHolder
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.DrawingToolViewHolder
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.OnDrawingToolClick

class DrawingToolAdapter(private val adapterListener: OnDrawingToolClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<DrawingToolItem>()
    private var showHeaders: Boolean = false

    override fun getItemViewType(position: Int): Int {
        return if (
            showHeaders && (position == 0 || items[position].section != items[position - 1].section)
        ) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_TOOL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> DrawingToolHeaderViewHolder(
                ItemDrawingToolHeaderBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            VIEW_TYPE_TOOL -> DrawingToolViewHolder(
                ItemDrawingToolBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> (holder as DrawingToolHeaderViewHolder)
                .bind(items[position].section)
            VIEW_TYPE_TOOL -> (holder as DrawingToolViewHolder)
                .bind(items[position], adapterListener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(value: List<DrawingToolItem>, showHeaders: Boolean) {
        this.showHeaders = showHeaders
        items = if (showHeaders) {
            val groupedItems = value.groupBy { it.section }
            val list = value.toMutableList()
            groupedItems.keys
                .forEach { section ->
                    list.findLast { it.section == section }?.let { item ->
                        val index = list.indexOf(item)
                        list.add(index, groupedItems[section]!![0])
                    }
                }
            list
        } else {
            value
        }
        notifyDataSetChanged()
    }

    fun selectItem(item: DrawingToolItem) {
        // Uncheck the previous item
        items
            .find { it.isSelected }
            ?.let {
                it.isSelected = false
                notifyItemChanged(items.indexOf(it))
            }
        val index = items.indexOf(item)
        items[index].isSelected = true
        notifyItemChanged(index)
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_TOOL = 1
    }
}
