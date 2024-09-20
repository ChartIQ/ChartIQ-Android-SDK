package com.chartiq.demo.ui.chart.drawingtools

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ui.chart.DrawingTools
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolCategory
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolItem
import com.chartiq.sdk.model.drawingtool.DrawingTool

class DrawingToolViewModel(private val appPrefs: ApplicationPrefs) : ViewModel() {

    private val originalDrawingToolList = DrawingTools.values().map {
        DrawingToolItem(
            it.tool,
            it.iconRes,
            it.nameRes,
            it.category,
            it.section
        )
    }

    val drawingToolList = MutableLiveData<List<DrawingToolItem>>(listOf())

    val category = MutableLiveData(DrawingToolCategory.ALL)

    fun favoriteItem(item: DrawingToolItem) {
        originalDrawingToolList.find { it == item }?.isStarred = !item.isStarred
    }

    fun saveDrawingTool(tool: DrawingTool) {
        appPrefs.saveDrawingTool(tool)
    }

    fun filterItemsByCategory(category: DrawingToolCategory) {
        this.category.value = category
        drawingToolList.value = when (category) {
            DrawingToolCategory.ALL -> originalDrawingToolList
            DrawingToolCategory.FAVORITES -> originalDrawingToolList
                .filter { item -> item.isStarred }
            else -> originalDrawingToolList
                .filter { item -> item.category == category }
        }
    }

    fun onPause() {
        saveFavoriteDrawingTools()
    }

    fun setupList() {
        val favoriteTools = appPrefs.getFavoriteDrawingTools()
        val selectedDrawingTool = appPrefs.getDrawingTool()
        drawingToolList.value = originalDrawingToolList.onEach {
            if (favoriteTools.contains(it.tool)) {
                it.isStarred = true
            }
            it.isSelected = it.tool == selectedDrawingTool && it.tool != DrawingTool.NONE
        }
    }

    private fun saveFavoriteDrawingTools() {
        val favoriteDrawingTools = originalDrawingToolList
            .filter { it.isStarred }
            .map { it.tool }
            .toHashSet()
        appPrefs.saveFavoriteDrawingTools(favoriteDrawingTools)
    }

    class DrawingToolViewModelFactory(private val prefs: ApplicationPrefs) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(ApplicationPrefs::class.java).newInstance(prefs)
        }
    }
}
