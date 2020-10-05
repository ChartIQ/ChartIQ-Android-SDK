package com.chartiq.demo.ui.chart.drawingtools.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolBinding
import com.chartiq.demo.databinding.ItemDrawingToolHeaderBinding
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.DrawingToolHeaderViewHolder
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.DrawingToolViewHolder
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.OnDrawingToolClick

class DrawingToolAdapter(private val list: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnDrawingToolClick {

    private var items = list
    private var previousSelectedPosition: Int? = null

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DrawingToolHeaderItem -> VIEW_TYPE_HEADER
            is DrawingToolItem -> VIEW_TYPE_TOOL
            else -> throw IllegalStateException()
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
        when (holder) {
            is DrawingToolHeaderViewHolder -> holder.bind(items[position] as DrawingToolHeaderItem)
            is DrawingToolViewHolder -> holder.bind(
                items[position] as DrawingToolItem,
                this,
                position
            )

        }
    }

    override fun getItemCount(): Int = items.size

    override fun onDrawingToolClick(position: Int) {
        val parentPosition = list.indexOf(items[position])
        list.forEachIndexed { index, it ->
            if (it is DrawingToolItem) {
                if (it.isSelected) {
                    previousSelectedPosition = index
                }
                it.isSelected = index == parentPosition
            }
        }
        previousSelectedPosition?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }

    override fun onFavoriteChecked(position: Int, value: Boolean) {
        val parentPosition = list.indexOf(items[position])
        (list[parentPosition] as DrawingToolItem).isStarred = value
    }

    fun filterItemsByCategory(category: DrawingToolCategory) {
        when (category) {
            DrawingToolCategory.ALL -> items = list
            DrawingToolCategory.FAVORITES -> items = list.filter { item ->
                if (item is DrawingToolHeaderItem) {
                    return@filter false
                } else {
                    (item as DrawingToolItem).isStarred
                }
            }
            else -> items = list.filter { item ->
                if (item is DrawingToolHeaderItem) {
                    return@filter false
                } else {
                    (item as DrawingToolItem).category == category
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getFavoriteItems(): List<DrawingToolItem> = items.filter { item ->
        if (item is DrawingToolHeaderItem) {
            return@filter false
        } else {
            (item as DrawingToolItem).isStarred
        }
    } as List<DrawingToolItem>

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_TOOL = 1
    }
}
