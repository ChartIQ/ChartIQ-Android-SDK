package com.chartiq.demo.ui

import android.content.Context
import android.graphics.drawable.InsetDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

open class LineItemDecoration(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    marginStart: Int = 0,
    marginTop: Int = 0,
    marginEnd: Int = 0,
    marginBottom: Int = 0
) : DividerItemDecoration(context, orientation) {
    init {
        val styledAttributes =
            context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        val lineDrawable = styledAttributes.getDrawable(0)
        val dividerDrawable =
            InsetDrawable(lineDrawable, marginStart, marginTop, marginEnd, marginBottom)
        styledAttributes.recycle()
        setDrawable(dividerDrawable)
    }

    class Default(context: Context) : LineItemDecoration(
        context = context,
        marginStart = context.resources.getDimensionPixelSize(R.dimen.list_item_decorator_margin)
    )
}
