package com.chartiq.demo.ui.chart.panel.settings

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

// Every third element should have space along with the divider line drawn below its actual size
class DeviationItemDecoration(
    private val context: Context,
    private val groupingSpace: Int = context.resources.getDimensionPixelOffset(R.dimen.margin_large)
) : RecyclerView.ItemDecoration() {

    private val bounds = Rect()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if ((parent.getChildAdapterPosition(view) + 1) % 3 == 0) {
            outRect.bottom = groupingSpace
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        val lineDrawable = styledAttributes.getDrawable(0)
        val marginStart = context.resources.getDimensionPixelSize(R.dimen.list_item_decorator_margin)
        val divider = InsetDrawable(lineDrawable, marginStart, 0, 0, 0)
        styledAttributes.recycle()

        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            if ((i + 1) % 3 != 0) {
                val bottom: Int = bounds.bottom + Math.round(child.translationY)
                val top: Int = bottom - divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            } else {
                val bottom: Int = bounds.bottom + Math.round(child.translationY) - groupingSpace
                val top: Int = bottom - divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }
        canvas.restore()
    }
}
