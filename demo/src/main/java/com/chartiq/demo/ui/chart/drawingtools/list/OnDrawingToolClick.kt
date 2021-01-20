package com.chartiq.demo.ui.chart.drawingtools.list

import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolItem

interface OnDrawingToolClick {

    fun onDrawingToolClick(item: DrawingToolItem)

    fun onFavoriteCheck(item: DrawingToolItem)
}
