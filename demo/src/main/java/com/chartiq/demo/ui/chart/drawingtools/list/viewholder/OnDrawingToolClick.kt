package com.chartiq.demo.ui.chart.drawingtools.list.viewholder

import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItem

interface OnDrawingToolClick {

    fun onDrawingToolClick(item: DrawingToolItem)

    fun onFavoriteChecked(item: DrawingToolItem)
}
