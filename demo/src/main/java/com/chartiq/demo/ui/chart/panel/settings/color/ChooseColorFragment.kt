package com.chartiq.demo.ui.chart.panel.settings.color

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChooseColorBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.colorpicker.ColorItem
import com.chartiq.demo.ui.common.colorpicker.ColorViewHolderConfiguration
import com.chartiq.demo.ui.common.colorpicker.ColorsAdapter
import com.chartiq.demo.ui.common.colorpicker.convertStringColorToInt
import com.chartiq.demo.ui.common.colorpicker.findColorIndex

class ChooseColorFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseColorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseColorBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            colorsToolbar.setNavigationOnClickListener {
                dismiss()
            }
            colorsRecyclerView.apply {
                val colorsAdapter = ColorsAdapter()
                val colorList = getColors()
                val selectedIndex = requireArguments().getString(ARG_SELECTED_COLOR)?.let {
                    val color = convertStringColorToInt(it, resources)
                    findColorIndex(colorList, color)
                }

                colorsAdapter.items =
                    colorList.mapIndexed { index, it -> it.copy(isSelected = index == selectedIndex) }
                colorsAdapter.viewHolderConfiguration = ColorViewHolderConfiguration(
                    minWidth = resources.getDimensionPixelSize(R.dimen.list_item_color_min_width_fullscreen),
                    minHeight = resources.getDimensionPixelSize(R.dimen.list_item_color_min_height_fullscreen)
                )
                colorsAdapter.listener = OnSelectItemListener {
                    val parameter = requireArguments().getString(ARG_PARAMETER)
                        ?: throw IllegalStateException("No drawing parameter was passed to the fragment")
                    (targetFragment as DialogFragmentListener).onChooseColor(parameter, it.color)

                    dismiss()
                }

                adapter = colorsAdapter
                post { selectedIndex?.let { smoothScrollToPosition(it) } }
            }
        }
    }

    private fun getColors(): List<ColorItem> {
        val colors = resources.obtainTypedArray(R.array.colors)
        val colorsList = mutableListOf<ColorItem>()
        for (index in 0 until colors.length()) {
            colorsList.add(
                ColorItem(colors.getColor(index, Color.WHITE))
            )
        }
        colors.recycle()
        return colorsList
    }

    companion object {
        fun getInstance(param: String, selectedColor: String): ChooseColorFragment {
            val dialog = ChooseColorFragment()
            dialog.arguments = bundleOf(
                ARG_PARAMETER to param,
                ARG_SELECTED_COLOR to selectedColor
            )
            return dialog
        }

        private const val ARG_PARAMETER = "choose.color.argument.parameter"
        private const val ARG_SELECTED_COLOR = "choose.color.argument.color.selected"
    }

    interface DialogFragmentListener {

        fun onChooseColor(parameter: String, color: Int)
    }
}
