package com.chartiq.demo.ui.chart.drawingtools.list.viewholder

interface OnDrawingToolClick {

    fun onDrawingToolClick(position: Int)

    fun onFavoriteChecked(position: Int, value: Boolean)
}
