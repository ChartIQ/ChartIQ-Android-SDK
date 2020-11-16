package com.chartiq.demo.ui.study.studydetails

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R


class StudyDetailsItemDecorator(
    context: Context,
) : DividerItemDecoration(context, RecyclerView.VERTICAL) {

    private var divider: Drawable? = null
    private val marginStart = context.resources.getDimensionPixelSize(R.dimen.list_item_decorator_margin)

    init {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)
            val viewType = parent.adapter?.getItemViewType(adapterPosition)
            if (viewType != ParamViewType.TEXT.ordinal && viewType != ParamViewType.NUMBER.ordinal) {
                divider?.apply {
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + intrinsicHeight
                    setBounds(left + marginStart, top, right, bottom)
                    draw(c)
                }
            }
        }
    }
}
