package com.chartiq.demo.ui.chart.drawingtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolCategory
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.OnDrawingToolClick
import com.chartiq.demo.util.Event

class DrawingToolViewModel : ViewModel(), OnDrawingToolClick {

    private val drawingToolSelectEvent = MutableLiveData<Event<DrawingToolItem>>()
    private val drawingToolFavoriteClickEvent = MutableLiveData<Event<DrawingToolItem>>()

    fun filterItemsByCategory(
        category: DrawingToolCategory,
        list: List<DrawingToolItem>
    ): List<DrawingToolItem> {
        return when (category) {
            DrawingToolCategory.ALL -> list
            DrawingToolCategory.FAVORITES -> list
                .filter { item -> item.isStarred }
            else -> list
                .filter { item -> item.category == category }
        }
    }

    override fun onDrawingToolClick(item: DrawingToolItem) {
        drawingToolSelectEvent.value = Event(item)
    }

    override fun onFavoriteChecked(item: DrawingToolItem) {
        drawingToolFavoriteClickEvent.value = Event(item)
    }
}
