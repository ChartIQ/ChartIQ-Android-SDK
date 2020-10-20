package com.chartiq.demo.ui.chart.drawingtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentDrawingToolBinding
import com.chartiq.demo.ui.chart.drawingtools.list.*
import com.chartiq.sdk.model.DrawingTool
import com.google.android.material.tabs.TabLayout

class DrawingToolFragment : Fragment() {

    private lateinit var binding: FragmentDrawingToolBinding
    private lateinit var viewModel: DrawingToolViewModel
    private lateinit var drawingToolAdapter: DrawingToolAdapter
    private lateinit var allToolsList: List<DrawingToolItem>
    private val appPrefs by lazy {
        ApplicationPrefs.Default(requireContext())
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
                if (category == DrawingToolCategory.ALL) {
                    drawingToolAdapter.setItems(allToolsList, true)
                } else {
                    val list = viewModel.filterItemsByCategory(category, allToolsList)
                    drawingToolAdapter.setItems(list, false)
                }
                binding.noFavoriteDrawingToolsPlaceHolder.root.visibility =
                    if (category == DrawingToolCategory.FAVORITES && allToolsList.none { it.isStarred }) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrawingToolBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DrawingToolViewModel::class.java)
        viewModel.apply {
            drawingToolFavoriteClickEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { item ->
                    allToolsList.find { it == item }?.isStarred = !item.isStarred
                }
            }
            drawingToolSelectEvent.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { item ->
                    drawingToolAdapter.selectItem(item)
                    allToolsList.find { it == item }?.isSelected = true
                }
            }
        }

        setupViews()
        return binding.root
    }

    override fun onPause() {
        val favoriteDrawingTools = allToolsList
            .filter { it.isStarred }
            .mapTo(HashSet()) { it.tool }
        val selectedDrawingTool = allToolsList
            .find { it.isSelected }?.tool ?: DrawingTool.NO_TOOL
        appPrefs.run {
            saveFavoriteDrawingTools(favoriteDrawingTools)
            saveDrawingTool(selectedDrawingTool)
        }
        super.onPause()
    }

    private fun setupViews() {
        setupList()

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
    }

    private fun setupList() {
        val favoriteTools = appPrefs.getFavoriteDrawingTools()
        val selectedDrawingTool = appPrefs.getDrawingTool()
        allToolsList = DEFAULT_TOOLS_LIST.onEach {
            if (favoriteTools.toString() == it.tool.value) {
                it.isStarred = true
            }
            it.isSelected = it.tool == selectedDrawingTool && it.tool != DrawingTool.NO_TOOL
        }
        drawingToolAdapter = DrawingToolAdapter(viewModel)
        drawingToolAdapter.setItems(allToolsList, true)
    }

    private fun showClearAllExistingDrawingDialog() {
        AlertDialog.Builder(requireContext(), R.style.NegativeAlertDialogTheme)
            .setTitle(R.string.drawing_tool_clear_drawings_title)
            .setMessage(R.string.drawing_tool_clear_drawings_message)
            .setNegativeButton(R.string.drawing_tool_clear_drawings_cancel) { dialog, _ -> Unit }
            .setPositiveButton(R.string.drawing_tool_clear_drawings_confirm) { dialog, _ ->
                // TODO: implement onRestoreClick()
            }
            .create()
            .show()
    }

    private fun showRestoreDefaultDialog() {
        AlertDialog.Builder(requireContext(), R.style.PositiveAlertDialogTheme)
            .setTitle(R.string.drawing_tool_restore_default_parameters_title)
            .setMessage(R.string.drawing_tool_restore_default_parameters_message)
            .setNegativeButton(R.string.drawing_tool_restore_default_parameters_cancel) { dialog, _ -> Unit }
            .setPositiveButton(R.string.drawing_tool_restore_default_parameters_confirm) { dialog, _ ->
                // TODO: implement onRestoreClick()
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

        private val DEFAULT_TOOLS_LIST = listOf(
            DrawingToolItem(
                DrawingTool.NO_TOOL,
                R.drawable.ic_drawing_tool_no_tool,
                R.string.drawing_tool_no_tool,
                DrawingToolCategory.NONE,
                DrawingToolSection.OTHER
            ),
            DrawingToolItem(
                DrawingTool.MEASURE,
                R.drawable.ic_drawing_tool_measure,
                R.string.drawing_tool_measure,
                DrawingToolCategory.NONE,
                DrawingToolSection.OTHER
            ),
            DrawingToolItem(
                DrawingTool.ANNOTATION,
                R.drawable.ic_drawing_tool_annotation,
                R.string.drawing_tool_annotation,
                DrawingToolCategory.TEXT,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.ARROW,
                R.drawable.ic_drawing_tool_arrow,
                R.string.drawing_tool_arrow,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.AVERAGE_LINE,
                R.drawable.ic_drawing_tool_average_line,
                R.string.drawing_tool_average_line,
                DrawingToolCategory.STATISTICS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CALLOUT,
                R.drawable.ic_drawing_tool_callout,
                R.string.drawing_tool_callout,
                DrawingToolCategory.TEXT,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CHANNEL,
                R.drawable.ic_drawing_tool_channel,
                R.string.drawing_tool_channel,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CHECK,
                R.drawable.ic_drawing_tool_check,
                R.string.drawing_tool_check,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CONTINUOUS_LINE,
                R.drawable.ic_drawing_tool_continuous_line,
                R.string.drawing_tool_continuous_line,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CROSS,
                R.drawable.ic_drawing_tool_cross,
                R.string.drawing_tool_cross,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.CROSSLINE,
                R.drawable.ic_drawing_tool_crossline,
                R.string.drawing_tool_crossline,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.DOODLE,
                R.drawable.ic_drawing_tool_doodle,
                R.string.drawing_tool_doodle,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.ELLIOTT_WAVE,
                R.drawable.ic_drawing_tool_elliott_wave,
                R.string.drawing_tool_elliott_wave,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.ELLIPSE,
                R.drawable.ic_drawing_tool_ellipse,
                R.string.drawing_tool_ellipse,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FIB_ARC,
                R.drawable.ic_drawing_tool_fib_arc,
                R.string.drawing_tool_fib_arc,
                DrawingToolCategory.FIBONACCI,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FIB_FAN,
                R.drawable.ic_drawing_tool_fib_fan,
                R.string.drawing_tool_fib_fan,
                DrawingToolCategory.FIBONACCI,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FIB_PROJECTION,
                R.drawable.ic_drawing_tool_fib_projection,
                R.string.drawing_tool_fib_projection,
                DrawingToolCategory.FIBONACCI,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FIB_RETRACEMENT,
                R.drawable.ic_drawing_tool_fib_retracement,
                R.string.drawing_tool_fib_retracement,
                DrawingToolCategory.FIBONACCI,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FIB_TIME_ZONE,
                R.drawable.ic_drawing_tool_fib_time_zone,
                R.string.drawing_tool_fib_time_zone,
                DrawingToolCategory.FIBONACCI,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.FOCUS,
                R.drawable.ic_drawing_tool_focus,
                R.string.drawing_tool_focus,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.GANN_FAN,
                R.drawable.ic_drawing_tool_gann_fan,
                R.string.drawing_tool_gann_fan,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.GARTLEY,
                R.drawable.ic_drawing_tool_gartley,
                R.string.drawing_tool_gartley,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.HEART,
                R.drawable.ic_drawing_tool_heart,
                R.string.drawing_tool_heart,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.HORIZONTAL_LINE,
                R.drawable.ic_drawing_tool_horizontal_line,
                R.string.drawing_tool_horizontal_line,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.LINE,
                R.drawable.ic_drawing_tool_line,
                R.string.drawing_tool_line,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.PITCHFORK,
                R.drawable.ic_drawing_tool_pitchfork,
                R.string.drawing_tool_pitchfork,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.QUADRANT_LINES,
                R.drawable.ic_drawing_tool_quadrant_lines,
                R.string.drawing_tool_quadrant_lines,
                DrawingToolCategory.STATISTICS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.RAY,
                R.drawable.ic_drawing_tool_ray,
                R.string.drawing_tool_ray,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.RECTANGLE,
                R.drawable.ic_drawing_tool_rectangle,
                R.string.drawing_tool_rectangle,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.REGRESSION_LINE,
                R.drawable.ic_drawing_tool_regression_line,
                R.string.drawing_tool_regression_line,
                DrawingToolCategory.STATISTICS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.SEGMENT,
                R.drawable.ic_drawing_tool_segment,
                R.string.drawing_tool_segment,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.STAR,
                R.drawable.ic_drawing_tool_star,
                R.string.drawing_tool_star,
                DrawingToolCategory.MARKINGS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.SPEED_RESISTANCE_ARC,
                R.drawable.ic_drawing_tool_speed_resistance_arc,
                R.string.drawing_tool_speed_resistance_arc,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.SPEED_RESISTANCE_LINE,
                R.drawable.ic_drawing_tool_speed_resistance_line,
                R.string.drawing_tool_speed_resistance_line,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.TIME_CYCLE,
                R.drawable.ic_drawing_tool_time_cycle,
                R.string.drawing_tool_time_cycle,
                DrawingToolCategory.TECHNICALS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.TIRONE_LEVELS,
                R.drawable.ic_drawing_tool_tirone_levels,
                R.string.drawing_tool_tirone_levels,
                DrawingToolCategory.STATISTICS,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.TREND_LINE,
                R.drawable.ic_drawing_tool_trend_line,
                R.string.drawing_tool_trend_line,
                DrawingToolCategory.TEXT,
                DrawingToolSection.MAIN
            ),
            DrawingToolItem(
                DrawingTool.VERTICAL_LINE,
                R.drawable.ic_drawing_tool_vertical_line,
                R.string.drawing_tool_vertical_line,
                DrawingToolCategory.LINES,
                DrawingToolSection.MAIN
            ),
        )
    }
}
