package com.chartiq.demo.ui.study

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R

class SimpleItemTouchCallBack(
    private val text: String,
    private val color: ColorDrawable,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE,
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
        val size = recyclerView.context.resources.getDimensionPixelSize(R.dimen.text_size_subtitle)
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = size.toFloat()
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
                textX = if (itemView.right + dX.toInt() < (itemView.right - bounds.width() * 2).toFloat()) {
                    (itemView.right - bounds.width()).toFloat()
                } else {
                    (bounds.left - bounds.right) / 2f
                }

            }
            else -> { // view is unSwiped
                mBackground.setBounds(0, 0, 0, 0)
                textX = (bounds.left - bounds.right) / 2f
            }
        }
        mBackground.draw(c)
        c.drawText(text, textX, textY, paint)
    }

    fun interface OnSwipeListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    }
}
