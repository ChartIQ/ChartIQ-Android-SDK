package com.chartiq.demo.ui.chart.panel.settings.line

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChooseLineBinding
import com.chartiq.demo.ui.chart.ChartFragment.Companion.DEFAULT_LINE_TYPE_LIST
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.linepicker.LineAdapter
import com.chartiq.demo.ui.common.linepicker.LineItem
import com.chartiq.demo.ui.common.linepicker.LineViewHolderConfiguration
import com.chartiq.demo.ui.common.linepicker.findLineIndex
import com.chartiq.sdk.model.drawingtool.LineType

class ChooseLineFragment : FullscreenDialogFragment() {

    private lateinit var binding: FragmentChooseLineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseLineBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            linesToolbar.setNavigationOnClickListener {
                dismiss()
            }
            linesRecyclerView.apply {
                val linesAdapter = LineAdapter()
                val selectedIndex =
                    arguments?.let {
                        val selectedLineType =
                            it.getParcelable<LineType>(ARG_SELECTED_LINE_TYPE)
                                ?: throw IllegalStateException("No line type was passed to the fragment")
                        val selectedLineWidth = it.getInt(ARG_SELECTED_LINE_WIDTH)

                        findLineIndex(DEFAULT_LINE_TYPE_LIST, selectedLineType, selectedLineWidth)
                    }

                linesAdapter.items =
                    DEFAULT_LINE_TYPE_LIST.mapIndexed { index, it -> it.copy(isSelected = index == selectedIndex) }
                linesAdapter.viewHolderConfiguration = LineViewHolderConfiguration(
                    minWidth = resources.getDimensionPixelSize(R.dimen.list_item_line_min_width_fullscreen),
                    minHeight = resources.getDimensionPixelSize(R.dimen.list_item_line_min_height_fullscreen)
                )
                linesAdapter.listener = OnSelectItemListener {
                    val lineParam = arguments?.getString(ARG_LINE_PARAMETER) ?: ""
                    val widthParam = arguments?.getString(ARG_WIDTH_PARAMETER) ?: ""

                    (targetFragment as DialogFragmentListener).onChooseLine(
                        lineParam,
                        widthParam,
                        it.lineType,
                        it.lineWidth
                    )
                    dismiss()
                }

                layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
                adapter = linesAdapter
                post { selectedIndex?.let { smoothScrollToPosition(it) } }
            }
        }
    }

    companion object {
        fun getInstance(lineParam: String, widthParam: String, lineType: LineType, lineWidth: Int): ChooseLineFragment {
            val dialog = ChooseLineFragment()
            dialog.arguments = bundleOf(
                ARG_LINE_PARAMETER to lineParam,
                ARG_WIDTH_PARAMETER to widthParam,
                ARG_SELECTED_LINE_TYPE to lineType,
                ARG_SELECTED_LINE_WIDTH to lineWidth,
            )
            return dialog
        }

        private const val SPAN_COUNT = 4
        private const val ARG_LINE_PARAMETER = "choose.line.type.argument.parameter"
        private const val ARG_WIDTH_PARAMETER = "choose.line.width.argument.parameter"
        private const val ARG_SELECTED_LINE_TYPE = "choose.line.argument.type.selected"
        private const val ARG_SELECTED_LINE_WIDTH = "choose.line.argument.width.selected"
    }

    interface DialogFragmentListener {

        fun onChooseLine(lineParam: String, widthParam: String, lineType: LineType, lineWidth: Int)
    }
}
