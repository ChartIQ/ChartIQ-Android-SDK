package com.chartiq.demo.ui.chart.drawingtools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentDrawingToolBinding
import com.chartiq.demo.ui.chart.drawingtools.list.*
import com.chartiq.sdk.model.DrawingTool
import com.google.android.material.tabs.TabLayout

class DrawingToolFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private lateinit var binding: FragmentDrawingToolBinding
    private lateinit var drawingToolAdapter: DrawingToolAdapter
    private val appPrefs by lazy {
        ApplicationPrefs.Default(requireContext())
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
        val favoriteDrawingTools = drawingToolAdapter.getFavoriteItems()
            .map { it.tool.value }
            .toSet()
        val selectedDrawingTool = drawingToolAdapter.getSelectedDrawingTool()
        appPrefs.run {
            saveFavoriteDrawingTools(favoriteDrawingTools)
            saveDrawingTool(selectedDrawingTool)
        }
        super.onPause()
    }

    private fun setupViews() {
        val favoriteTools = appPrefs.getFavoriteDrawingTools()
        val selectedDrawingTool = appPrefs.getDrawingTool()
        val toolList = DEFAULT_LIST.onEach {
            if (it is DrawingToolItem) {
                if (favoriteTools.contains(it.tool.value)) {
                    it.isStarred = true
                }
                if (it.tool == selectedDrawingTool && it.tool != DrawingTool.NO_TOOL) {
                    it.isSelected = true
                }
            }
        }
        drawingToolAdapter = DrawingToolAdapter(toolList)
        with(binding) {
            drawingToolRecyclerView.apply {
                adapter = drawingToolAdapter
                addItemDecoration(DrawingToolItemDecorator(requireContext()))
            }

            drawingToolTabLayout.apply {
                addOnTabSelectedListener(this@DrawingToolFragment)
            }

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
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            val category = when (it.position) {
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
            drawingToolAdapter.filterItemsByCategory(category)
            binding.noFavoriteDrawingToolsPlaceHolder.root.visibility =
                if (category == DrawingToolCategory.FAVORITES
                    && drawingToolAdapter.getFavoriteItems().isEmpty()
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

    override fun onTabReselected(tab: TabLayout.Tab?) = Unit

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

        private val DEFAULT_LIST = listOf(
            DrawingToolHeaderItem(R.string.drawing_tool_header_other_tools),
            DrawingToolItem(
                DrawingTool.NO_TOOL,
                R.drawable.ic_drawing_tool_no_tool,
                R.string.drawing_tool_no_tool,
                DrawingToolCategory.NONE
            ),
            DrawingToolItem(
                DrawingTool.MEASURE,
                R.drawable.ic_drawing_tool_measure,
                R.string.drawing_tool_measure,
                DrawingToolCategory.NONE
            ),
            DrawingToolHeaderItem(R.string.drawing_tool_header_main_tools),
            DrawingToolItem(
                DrawingTool.ANNOTATION,
                R.drawable.ic_drawing_tool_annotation,
                R.string.drawing_tool_annotation,
                DrawingToolCategory.TEXT
            ),
            DrawingToolItem(
                DrawingTool.ARROW,
                R.drawable.ic_drawing_tool_arrow,
                R.string.drawing_tool_arrow,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.AVERAGE_LINE,
                R.drawable.ic_drawing_tool_average_line,
                R.string.drawing_tool_average_line,
                DrawingToolCategory.STATISTICS
            ),
            DrawingToolItem(
                DrawingTool.CALLOUT,
                R.drawable.ic_drawing_tool_callout,
                R.string.drawing_tool_callout,
                DrawingToolCategory.TEXT
            ),
            DrawingToolItem(
                DrawingTool.CHANNEL,
                R.drawable.ic_drawing_tool_channel,
                R.string.drawing_tool_channel,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.CHECK,
                R.drawable.ic_drawing_tool_check,
                R.string.drawing_tool_check,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.CONTINUOUS_LINE,
                R.drawable.ic_drawing_tool_continuous_line,
                R.string.drawing_tool_continuous_line,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.CROSS,
                R.drawable.ic_drawing_tool_cross,
                R.string.drawing_tool_cross,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.CROSSLINE,
                R.drawable.ic_drawing_tool_crossline,
                R.string.drawing_tool_crossline,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.DOODLE,
                R.drawable.ic_drawing_tool_doodle,
                R.string.drawing_tool_doodle,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.ELLIOTT_WAVE,
                R.drawable.ic_drawing_tool_elliott_wave,
                R.string.drawing_tool_elliott_wave,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.ELLIPSE,
                R.drawable.ic_drawing_tool_ellipse,
                R.string.drawing_tool_ellipse,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.FIB_ARC,
                R.drawable.ic_drawing_tool_fib_arc,
                R.string.drawing_tool_fib_arc,
                DrawingToolCategory.FIBONACCI
            ),
            DrawingToolItem(
                DrawingTool.FIB_FAN,
                R.drawable.ic_drawing_tool_fib_fan,
                R.string.drawing_tool_fib_fan,
                DrawingToolCategory.FIBONACCI
            ),
            DrawingToolItem(
                DrawingTool.FIB_PROJECTION,
                R.drawable.ic_drawing_tool_fib_projection,
                R.string.drawing_tool_fib_projection,
                DrawingToolCategory.FIBONACCI
            ),
            DrawingToolItem(
                DrawingTool.FIB_RETRACEMENT,
                R.drawable.ic_drawing_tool_fib_retracement,
                R.string.drawing_tool_fib_retracement,
                DrawingToolCategory.FIBONACCI
            ),
            DrawingToolItem(
                DrawingTool.FIB_TIME_ZONE,
                R.drawable.ic_drawing_tool_fib_time_zone,
                R.string.drawing_tool_fib_time_zone,
                DrawingToolCategory.FIBONACCI
            ),
            DrawingToolItem(
                DrawingTool.FOCUS,
                R.drawable.ic_drawing_tool_focus,
                R.string.drawing_tool_focus,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.GANN_FAN,
                R.drawable.ic_drawing_tool_gann_fan,
                R.string.drawing_tool_gann_fan,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.GARTLEY,
                R.drawable.ic_drawing_tool_gartley,
                R.string.drawing_tool_gartley,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.HEART,
                R.drawable.ic_drawing_tool_heart,
                R.string.drawing_tool_heart,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.HORIZONTAL_LINE,
                R.drawable.ic_drawing_tool_horizontal_line,
                R.string.drawing_tool_horizontal_line,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.LINE,
                R.drawable.ic_drawing_tool_line,
                R.string.drawing_tool_line,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.PITCHFORK,
                R.drawable.ic_drawing_tool_pitchfork,
                R.string.drawing_tool_pitchfork,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.QUADRANT_LINES,
                R.drawable.ic_drawing_tool_quadrant_lines,
                R.string.drawing_tool_quadrant_lines,
                DrawingToolCategory.STATISTICS
            ),
            DrawingToolItem(
                DrawingTool.RAY,
                R.drawable.ic_drawing_tool_ray,
                R.string.drawing_tool_ray,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.RECTANGLE,
                R.drawable.ic_drawing_tool_rectangle,
                R.string.drawing_tool_rectangle,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.REGRESSION_LINE,
                R.drawable.ic_drawing_tool_regression_line,
                R.string.drawing_tool_regression_line,
                DrawingToolCategory.STATISTICS
            ),
            DrawingToolItem(
                DrawingTool.SEGMENT,
                R.drawable.ic_drawing_tool_segment,
                R.string.drawing_tool_segment,
                DrawingToolCategory.LINES
            ),
            DrawingToolItem(
                DrawingTool.STAR,
                R.drawable.ic_drawing_tool_star,
                R.string.drawing_tool_star,
                DrawingToolCategory.MARKINGS
            ),
            DrawingToolItem(
                DrawingTool.SPEED_RESISTANCE_ARC,
                R.drawable.ic_drawing_tool_speed_resistance_arc,
                R.string.drawing_tool_speed_resistance_arc,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.SPEED_RESISTANCE_LINE,
                R.drawable.ic_drawing_tool_speed_resistance_line,
                R.string.drawing_tool_speed_resistance_line,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.TIME_CYCLE,
                R.drawable.ic_drawing_tool_time_cycle,
                R.string.drawing_tool_time_cycle,
                DrawingToolCategory.TECHNICALS
            ),
            DrawingToolItem(
                DrawingTool.TIRONE_LEVELS,
                R.drawable.ic_drawing_tool_tirone_levels,
                R.string.drawing_tool_tirone_levels,
                DrawingToolCategory.STATISTICS
            ),
            DrawingToolItem(
                DrawingTool.TREND_LINE,
                R.drawable.ic_drawing_tool_trend_line,
                R.string.drawing_tool_trend_line,
                DrawingToolCategory.TEXT
            ),
            DrawingToolItem(
                DrawingTool.VERTICAL_LINE,
                R.drawable.ic_drawing_tool_vertical_line,
                R.string.drawing_tool_vertical_line,
                DrawingToolCategory.LINES
            ),
        )
    }
}
