package com.chartiq.demo.ui.chart.fullview

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.chartiq.demo.R

open class CollapseButtonOnSwipeTouchListener(
    private val constraintLayout: ConstraintLayout,
    context: Context,
    private val onSwipe: () -> Unit
) : ViewOnSwipeTouchListener(context) {

    private val set by lazy {
        val set = ConstraintSet()
        set.clone(constraintLayout)
        set
    }
    private val margin = context.resources.getDimensionPixelSize(R.dimen.margin_extra_large)

    override fun onSwipeRight() {
        onSwipe()
        with(set) {
            clear(R.id.collapseFullviewCheckBox, ConstraintSet.START)
            connect(
                R.id.collapseFullviewCheckBox,
                ConstraintSet.END,
                R.id.chartIqView,
                ConstraintSet.END
            )
            addMargin()
            applyTo(constraintLayout)
        }
    }

    override fun onSwipeLeft() {
        onSwipe()
        with(set) {
            clear(R.id.collapseFullviewCheckBox, ConstraintSet.END)
            connect(
                R.id.collapseFullviewCheckBox,
                ConstraintSet.START,
                R.id.chartIqView,
                ConstraintSet.START
            )
            addMargin()
            applyTo(constraintLayout)
        }
    }

    override fun onSwipeTop() {
        onSwipe()
        with(set) {
            clear(R.id.collapseFullviewCheckBox, ConstraintSet.BOTTOM)
            connect(
                R.id.collapseFullviewCheckBox,
                ConstraintSet.TOP,
                R.id.chartIqView,
                ConstraintSet.TOP
            )
            addMargin()
            applyTo(constraintLayout)
        }
    }

    override fun onSwipeBottom() {
        onSwipe()
        with(set) {
            clear(R.id.collapseFullviewCheckBox, ConstraintSet.TOP)
            connect(
                R.id.collapseFullviewCheckBox,
                ConstraintSet.BOTTOM,
                R.id.chartIqView,
                ConstraintSet.BOTTOM
            )
            addMargin()
            applyTo(constraintLayout)
        }
    }

    private fun addMargin() {
        with(set) {
            setMargin(R.id.collapseFullviewCheckBox, ConstraintSet.TOP, margin)
            setMargin(R.id.collapseFullviewCheckBox, ConstraintSet.END, margin)
            setMargin(R.id.collapseFullviewCheckBox, ConstraintSet.BOTTOM, margin)
            setMargin(R.id.collapseFullviewCheckBox, ConstraintSet.START, margin)
        }
    }
}
