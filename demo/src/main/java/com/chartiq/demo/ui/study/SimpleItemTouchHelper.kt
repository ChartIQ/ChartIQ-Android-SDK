package com.chartiq.demo.ui.study

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SimpleItemTouchCallBack(
    private val text: String,
    private val color: ColorDrawable,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT
) {
    var onSwipeListener: OnSwipeListener? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipeListener?.onSwiped(viewHolder, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {

        super.onChildDraw(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val mBackground = color
        val text = text
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 40F
            textAlign = Paint.Align.CENTER
        }
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textX: Float
        val textY = (itemView.bottom - itemView.height / 2 + bounds.height() / 2).toFloat()
        when {
            dX > 0 -> { // Swiping to the right
                mBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )
                textX = 0f
            }
            dX < 0 -> { // Swiping to the left
                mBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top, itemView.right, itemView.bottom
                )
                textX = (itemView.right - bounds.width()).toFloat()
            }
            else -> { // view is unSwiped
                mBackground.setBounds(0, 0, 0, 0)
                textX = 0f
            }
        }
        mBackground.draw(c)
        Log.i("@@@", textY.toString())
        Log.i("@@@", "bounds.height() ${bounds.height()}")
        Log.i("@@@--", "${itemView.top} -- ${itemView.bottom}")
        c.drawText(text, textX, textY, paint)
    }

    interface OnSwipeListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    }
}
