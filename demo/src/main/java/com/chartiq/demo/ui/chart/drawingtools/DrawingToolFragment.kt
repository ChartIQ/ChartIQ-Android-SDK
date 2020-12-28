package com.chartiq.demo.ui.chart.drawingtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentDrawingToolBinding
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolAdapter
import com.chartiq.demo.ui.chart.drawingtools.list.DrawingToolItemDecorator
import com.chartiq.demo.ui.chart.drawingtools.list.OnDrawingToolClick
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolCategory
import com.chartiq.demo.ui.chart.drawingtools.list.model.DrawingToolItem
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.drawingtool.DrawingTool
import com.google.android.material.tabs.TabLayout

class DrawingToolFragment : Fragment() {

    private lateinit var binding: FragmentDrawingToolBinding
    private val viewModel: DrawingToolViewModel by viewModels(factoryProducer = {
        DrawingToolViewModel.DrawingToolViewModelFactory((requireActivity().application as ServiceLocator).applicationPreferences)
    })
    private val drawingToolAdapter = DrawingToolAdapter()
    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val tabOnSelectListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { tab ->
                val category = when (tab.position) {
                    TAB_ALL -> DrawingToolCategory.ALL
                    TAB_FAVORITES -> DrawingToolCategory.FAVORITES
                    TAB_TEXT -> DrawingToolCategory.TEXT
                    TAB_STATISTICS -> DrawingToolCategory.STATISTICS
                    TAB_TECHNICALS -> DrawingToolCategory.TECHNICALS
                    TAB_FIBONACCI -> DrawingToolCategory.FIBONACCI
                    TAB_MARKINGS -> DrawingToolCategory.MARKINGS
                    TAB_LINES -> DrawingToolCategory.LINES
                    else -> throw IllegalStateException()
                }
                viewModel.filterItemsByCategory(category)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    }
    private val drawingToolListener = object : OnDrawingToolClick {
        override fun onDrawingToolClick(item: DrawingToolItem) {
            viewModel.saveDrawingTool(item.tool)
            findNavController().navigateUp()
        }

        override fun onFavoriteCheck(item: DrawingToolItem) {
            viewModel.favoriteItem(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrawingToolBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    private fun setupViews() {
        drawingToolAdapter.listener = drawingToolListener
        viewModel.setupList()

        with(binding) {
            drawingToolToolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                menu.findItem(R.id.restore_default_parameters).setOnMenuItemClickListener {
                    showRestoreDefaultDialog()
                    true
                }
                menu.findItem(R.id.clear_existing_drawings).setOnMenuItemClickListener {
                    showClearAllExistingDrawingDialog()
                    true
                }
            }

            drawingToolTabLayout.addOnTabSelectedListener(tabOnSelectListener)
            drawingToolRecyclerView.apply {
                adapter = drawingToolAdapter
                addItemDecoration(DrawingToolItemDecorator(requireContext()))
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        with(viewModel) {
            drawingToolList.observe(viewLifecycleOwner) { list ->
                val showHeaders = category.value == DrawingToolCategory.ALL
                drawingToolAdapter.setItems(list, showHeaders)

                binding.noFavoriteDrawingToolsPlaceHolder.root.isVisible =
                    category.value == DrawingToolCategory.FAVORITES && list.isEmpty()
            }
        }
    }

    private fun showClearAllExistingDrawingDialog() {
        AlertDialog.Builder(requireContext(), R.style.NegativeAlertDialogTheme)
            .setTitle(R.string.drawing_tool_clear_drawings_title)
            .setMessage(R.string.drawing_tool_clear_drawings_message)
            .setNegativeButton(R.string.drawing_tool_clear_drawings_cancel) { _, _ -> Unit }
            .setPositiveButton(R.string.drawing_tool_clear_drawings_confirm) { _, _ ->
                chartIQ.clearDrawing()
            }
            .create()
            .show()
    }

    private fun showRestoreDefaultDialog() {
        AlertDialog.Builder(requireContext(), R.style.PositiveAlertDialogTheme)
            .setTitle(R.string.drawing_tool_restore_default_parameters_title)
            .setMessage(R.string.drawing_tool_restore_default_parameters_message)
            .setNegativeButton(R.string.drawing_tool_restore_default_parameters_cancel) { _, _ -> Unit }
            .setPositiveButton(R.string.drawing_tool_restore_default_parameters_confirm) { _, _ ->
                chartIQ.restoreDefaultDrawingConfig(DrawingTool.LINE, true)
            }
            .create()
            .show()
    }

    companion object {
        private const val TAB_ALL = 0
        private const val TAB_FAVORITES = 1
        private const val TAB_TEXT = 2
        private const val TAB_STATISTICS = 3
        private const val TAB_TECHNICALS = 4
        private const val TAB_FIBONACCI = 5
        private const val TAB_MARKINGS = 6
        private const val TAB_LINES = 7
    }
}
