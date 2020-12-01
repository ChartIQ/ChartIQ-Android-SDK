package com.chartiq.demo.ui.chart.drawingtools.list

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.ui.LineItemDecoration

class DrawingToolItemDecorator(context: Context) : LineItemDecoration(
    context,
    marginStart = context.resources.getDimensionPixelSize(R.dimen.drawing_tool_item_decorator_margin_start)
) {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val viewType = parent.adapter?.getItemViewType(position)
        if (viewType == DrawingToolAdapter.VIEW_TYPE_HEADER) {
            outRect.setEmpty()
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}
