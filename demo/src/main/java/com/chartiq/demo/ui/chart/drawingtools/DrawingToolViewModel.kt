package com.chartiq.demo.ui.chart.drawingtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.chart.DrawingTools
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolCategory
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem
import com.chartiq.demo.ui.chart.drawingtools.list.viewholder.OnDrawingToolClick
import com.chartiq.demo.util.Event
import com.chartiq.sdk.model.drawingtool.DrawingTool

class DrawingToolViewModel(private val appPrefs: ApplicationPrefs) : ViewModel(),
    OnDrawingToolClick {

    val drawingToolSelectEvent = MutableLiveData<Event<DrawingToolItem>>()
    val drawingToolFavoriteClickEvent = MutableLiveData<Event<DrawingToolItem>>()

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

    fun saveUserPreferences(toolsList: List<DrawingToolItem>) {
        val favoriteDrawingTools = toolsList
            .filter { it.isStarred }
            .map { it.tool }
            .toHashSet()
        val selectedDrawingTool = toolsList
            .find { it.isSelected }?.tool ?: DrawingTool.NONE
        with(appPrefs) {
            saveFavoriteDrawingTools(favoriteDrawingTools)
            saveDrawingTool(selectedDrawingTool)
        }
    }

    override fun onDrawingToolClick(item: DrawingToolItem) {
        appPrefs.saveDrawingTool(item.tool)
        drawingToolSelectEvent.value = Event(item)
    }

    override fun onFavoriteCheck(item: DrawingToolItem) {
        drawingToolFavoriteClickEvent.value = Event(item)
    }

    fun setupList(): List<DrawingToolItem> {
        val list = DrawingTools.values().map {
            DrawingToolItem(
                it.tool,
                it.iconRes,
                it.nameRes,
                it.category,
                it.section
            )
        }

        val favoriteTools = appPrefs.getFavoriteDrawingTools()
        val selectedDrawingTool = appPrefs.getDrawingTool()
        return list.onEach {
            if (favoriteTools.toString() == it.tool.value) {
                it.isStarred = true
            }
            it.isSelected = it.tool == selectedDrawingTool && it.tool != DrawingTool.NONE
        }
    }

    class DrawingToolViewModelFactory(private val prefs: ApplicationPrefs) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(ApplicationPrefs::class.java).newInstance(prefs)
        }
    }
}
