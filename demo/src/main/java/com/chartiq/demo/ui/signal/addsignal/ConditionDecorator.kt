package com.chartiq.demo.ui.signal.addsignal

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R


class ConditionDecorator(context: Context) : RecyclerView.ItemDecoration() {

    private val decorationView: Drawable

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.divider_and, null)
        decorationView = viewToBitmap(view, 500, 500)
    }

    fun viewToBitmap(view: View, width: Int, height: Int): Drawable {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        rect.right = decorationView.intrinsicWidth
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            parent.children
                .forEach { view ->
                    decorationView.drawSeparator(view, parent, canvas)
                }
        }
    }

    private fun Drawable.drawSeparator(view: View, parent: RecyclerView, canvas: Canvas) =
        apply {
            val left = view.right
            val top = parent.paddingTop
            val right = left + intrinsicWidth
            val bottom = top + intrinsicHeight - parent.paddingBottom
            bounds = Rect(left, top, right, bottom)
            draw(canvas)
        }

}
