package com.chartiq.demo.ui.chart.drawingtools.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemDrawingToolBinding
import com.chartiq.demo.databinding.ItemDrawingToolHeaderBinding
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolItem
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolSection

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
            VIEW_TYPE_HEADER -> DrawingToolViewHolder.DrawingToolHeaderViewHolder(
                ItemDrawingToolHeaderBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            VIEW_TYPE_TOOL -> DrawingToolViewHolder.RegularDrawingToolViewHolder(
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
            VIEW_TYPE_HEADER -> (holder as DrawingToolViewHolder.DrawingToolHeaderViewHolder)
                .bind(items[position].section)
            VIEW_TYPE_TOOL -> (holder as DrawingToolViewHolder.RegularDrawingToolViewHolder)
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
                    list.find { it.section == section }?.let { item ->
                        val index = list.indexOf(item)
                        list.add(
                            index,
                            groupedItems[section]!![0].copy(iconRes = 0, nameRes = 0)
                        )
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

    abstract class DrawingToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class DrawingToolHeaderViewHolder(private val binding: ItemDrawingToolHeaderBinding) :
            DrawingToolViewHolder(binding.root) {

            fun bind(section: DrawingToolSection) {
                binding.headerTextView.text = binding.root.resources.getString(section.stringRes)
            }
        }

        class RegularDrawingToolViewHolder(
            private val binding: ItemDrawingToolBinding
        ) : DrawingToolViewHolder(binding.root) {

            fun bind(item: DrawingToolItem, listener: OnDrawingToolClick) {
                with(binding) {
                    iconImageView.background = ContextCompat.getDrawable(root.context, item.iconRes)
                    toolNameTextView.text = root.resources.getString(item.nameRes)
                    checkImageView.visibility = if (item.isSelected) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    starIndicatorImageView.apply {
                        isChecked = item.isStarred
                        setOnCheckedChangeListener { button, _ ->
                            if (button.isPressed) {
                                listener.onFavoriteCheck(item)
                            }
                        }
                    }
                    binding.root.setOnClickListener {
                        listener.onDrawingToolClick(item)
                    }
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_TOOL = 1
    }
}
