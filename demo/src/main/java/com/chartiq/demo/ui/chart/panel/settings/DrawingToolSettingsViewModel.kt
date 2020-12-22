package com.chartiq.demo.ui.chart.panel.settings

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chartiq.demo.R
import com.chartiq.demo.network.model.DrawingParameter
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.drawingtool.*
import com.chartiq.sdk.model.drawingtool.drawingmanager.DrawingManager
import com.chartiq.sdk.model.drawingtool.elliottwave.Corrective
import com.chartiq.sdk.model.drawingtool.elliottwave.Decoration
import com.chartiq.sdk.model.drawingtool.elliottwave.Impulse
import com.google.gson.Gson
import java.util.*

class DrawingToolSettingsViewModel(
    private val chartIQHandler: ChartIQ,
    private val drawingManager: DrawingManager
) : ViewModel() {

    val parameters = MutableLiveData<Map<String, Any>>()
    val drawingTool = MutableLiveData<DrawingTool>()
    val settingsList = MutableLiveData<List<DrawingToolSettingsItem>>(emptyList())

    fun setupList(params: Map<String, Any>, isNestedSettings: Boolean) {
        val list = mutableListOf<DrawingToolSettingsItem>()
        val tool = drawingTool.value!!

        with(drawingManager) {
            if (isSupportingFillColor(tool)) {
                list.addFillColorModel(params)
            }
            if (isSupportingLineColor(tool)) {
                list.addLineColorModel(params)
            }
            if (isSupportingLineType(tool)) {
                list.addLineTypeModels(params)
            }
            if (isSupportingFont(tool)) {
                list.addFontModels(params)
            }
            if (isSupportingAxisLabel(tool)) {
                list.addAxisLabelModels(params)
            }
            if (isSupportingDeviations(tool)) {
                list.addDeviationModels(params)
            }
            if (isSupportingFibonacci(tool)) {
                list.addFibonacciModels(params)
            }
            if (isSupportingElliottWave(tool)) {
                list.addElliotWaveModels(params)
            }
        }

        if (isNestedSettings) {
            list.find { it is DrawingToolSettingsItem.Deviation }?.let { deviation ->
                deviation as DrawingToolSettingsItem.Deviation
                settingsList.value = deviation.settings
                return
            }
        }

        settingsList.value = list
    }

    fun refreshDrawingParameters() {
        chartIQHandler.getDrawingParameters(drawingTool.value!!) { params ->
            parameters.value = params
        }
    }

    fun updateAnnotationParameters(item: DrawingToolSettingsItem.Style) {
        val boldValue = if (item.isBold) {
            DrawingParameter.BOLD.value
        } else {
            DrawingParameter.BOLD_OFF.value
        }
        val italicValue = if (item.isItalic) {
            DrawingParameter.ITALIC.value
        } else {
            DrawingParameter.NORMAL.value
        }
        updateParameter(item.weightParam, boldValue)
        updateParameter(item.styleParam, italicValue)
    }

    fun updateSwitchParameter(parameter: String, value: Boolean) {
        // No refresh
        chartIQHandler.setDrawingParameter(parameter, value.toString())
    }

    fun updateParameter(parameter: String, value: String) {
        chartIQHandler.setDrawingParameter(parameter, value)
        refreshDrawingParameters()
    }

    fun updateColorParameter(parameter: String, color: Int) {
        updateParameter(parameter, color.toHexStringWithHash())
    }

    fun setDrawingTool(drawingTool: DrawingTool) {
        this.drawingTool.value = drawingTool
    }

    private fun MutableList<DrawingToolSettingsItem>.addFillColorModel(params: Map<String, Any>) {
        val colorParam = DrawingParameter.FILL_COLOR.value
        val color = params[colorParam].toString()
        add(
            DrawingToolSettingsItem.Color(
                R.string.drawing_tool_settings_title_fill_color,
                color,
                colorParam
            )
        )
    }

    private fun MutableList<DrawingToolSettingsItem>.addLineColorModel(params: Map<String, Any>) {
        val colorParam = DrawingParameter.LINE_COLOR.value
        val color = params[colorParam].toString()
        add(
            DrawingToolSettingsItem.Color(
                R.string.drawing_tool_settings_title_line_color,
                color,
                colorParam
            )
        )
    }

    private fun MutableList<DrawingToolSettingsItem>.addLineTypeModels(params: Map<String, Any>) {
        val lineType = params[DrawingParameter.LINE_TYPE.value].toString()
        val lineWidth = params[DrawingParameter.LINE_WIDTH.value].toString()
        add(
            DrawingToolSettingsItem.Line(
                R.string.drawing_tool_settings_title_line_type,
                LineType.valueOf(lineType.toUpperCase(Locale.getDefault())),
                lineWidth.toDouble().toInt()
            )
        )
    }

    private fun MutableList<DrawingToolSettingsItem>.addFontModels(params: Map<String, Any>) {
        val value = params[DrawingParameter.FONT.value].toString()
        val font = Gson().fromJson(value, Font::class.java)

        // family
        val familyParam = DrawingParameter.FAMILY.value
        val family = font.family.value
        add(
            DrawingToolSettingsItem.ChooseValue(
                R.string.drawing_tool_settings_title_font_family,
                family,
                familyParam,
                FontFamily.values().map {
                    OptionItem(it.value, it.value == family)
                }
            )
        )
        // style
        // is text italic
        val style = font.style.value
        val isItalic = style == DrawingParameter.ITALIC.value

        // is text bold
        // if value equals `300` it's normal, but it can also be string `bold`
        val isBold: Boolean = try {
            val weight = font.weight.toInt()
            !(weight != null && weight <= DrawingParameter.BOLD_OFF.value.toInt())
        } catch (e: NumberFormatException) {
            font.weight == DrawingParameter.BOLD.value
        }

        add(
            DrawingToolSettingsItem.Style(
                R.string.drawing_tool_settings_title_font_style,
                isBold,
                isItalic
            )
        )

        // size
        val sizeParam = DrawingParameter.SIZE.value
        val size = font.size
        add(
            DrawingToolSettingsItem.ChooseValue(
                R.string.drawing_tool_settings_title_font_size,
                size,
                sizeParam,
                FontSize.values().map {
                    OptionItem(it.pxValue, it.pxValue == size)
                }
            )
        )
    }

    private fun MutableList<DrawingToolSettingsItem>.addAxisLabelModels(params: Map<String, Any>) {
        val checked = params[DrawingParameter.AXIS_LABEL.value]?.toString()?.toBoolean() ?: false
        add(
            DrawingToolSettingsItem.Switch(
                R.string.drawing_tool_settings_title_axis_label,
                checked,
                DrawingParameter.AXIS_LABEL.value
            )
        )
    }

    private fun MutableList<DrawingToolSettingsItem>.addDeviationModels(params: Map<String, Any>) {
        val deviationList = mutableListOf<DrawingToolSettingsItem>()
        fun addColorModel(colorParam: DrawingParameter, @StringRes title: Int) {
            val color = params[colorParam.value].toString()
            deviationList.add(
                DrawingToolSettingsItem.Color(
                    title,
                    color,
                    colorParam.value
                )
            )
        }

        fun addBooleanModel(boolParam: DrawingParameter, @StringRes title: Int) {
            val value = params[boolParam.value]?.toString()
            val isChecked = if (value.isNullOrEmpty()) {
                false
            } else {
                value.toBoolean()
            }
            deviationList.add(
                DrawingToolSettingsItem.Switch(
                    title,
                    isChecked,
                    boolParam.value
                )
            )
        }

        fun addLineModel(
            typeParam: DrawingParameter,
            widthParam: DrawingParameter,
            @StringRes title: Int
        ) {
            val lineType = params[typeParam.value].toString()
            val lineWidth = params[widthParam.value].toString()
            deviationList.add(
                DrawingToolSettingsItem.Line(
                    title,
                    LineType.valueOf(lineType.toUpperCase(Locale.getDefault())),
                    lineWidth.toDouble().toInt(),
                    typeParam.value,
                    widthParam.value
                )
            )
        }

        addBooleanModel(DrawingParameter.ACTIVE_1, R.string.drawing_tool_settings_title_show_line_1)
        addColorModel(DrawingParameter.COLOR_1, R.string.drawing_tool_settings_title_line_1_color)
        addLineModel(
            DrawingParameter.PATTERN_1,
            DrawingParameter.LINE_WIDTH_1,
            R.string.drawing_tool_settings_title_show_line_1
        )
        addBooleanModel(DrawingParameter.ACTIVE_2, R.string.drawing_tool_settings_title_show_line_2)
        addColorModel(DrawingParameter.COLOR_2, R.string.drawing_tool_settings_title_line_2_color)
        addLineModel(
            DrawingParameter.PATTERN_2,
            DrawingParameter.LINE_WIDTH_2,
            R.string.drawing_tool_settings_title_line_2_type
        )
        addBooleanModel(DrawingParameter.ACTIVE_3, R.string.drawing_tool_settings_title_show_line_3)
        addColorModel(DrawingParameter.COLOR_3, R.string.drawing_tool_settings_title_line_3_color)
        addLineModel(
            DrawingParameter.PATTERN_3,
            DrawingParameter.LINE_WIDTH_3,
            R.string.drawing_tool_settings_title_line_3_type
        )
        add(
            DrawingToolSettingsItem.Deviation(
                R.string.drawing_tool_settings_title_deviations_settings,
                deviationList
            )
        )
    }

    // TODO: Review fibs aren't null verification
    private fun MutableList<DrawingToolSettingsItem>.addFibonacciModels(params: Map<String, Any>) {
        val fibsParam = DrawingParameter.FIBS.value
        if (params[fibsParam].toString() != "null") {
            val values = Gson().fromJson(params[fibsParam].toString(), Array<Fib>::class.java).toList()
            add(
                DrawingToolSettingsItem.ChooseValue(
                    R.string.drawing_tool_settings_title_config,
                    "",
                    fibsParam,
                    values.map {
                        val value = it.level.toString() + "%"
                        OptionItem(value, it.display)
                    },
                    isMultipleSelection = true,
                    hasCustomValueSupport = true
                )
            )
        }
    }

    private fun MutableList<DrawingToolSettingsItem>.addElliotWaveModels(params: Map<String, Any>) {
        fun addChooseValueModel(
            param: DrawingParameter,
            @StringRes title: Int,
            secondaryText: String,
            values: List<OptionItem>
        ) {
            add(
                DrawingToolSettingsItem.ChooseValue(
                    title,
                    secondaryText,
                    param.value,
                    values
                )
            )
        }

        val waveParameters = params[DrawingParameter.WAVE_PARAMETERS.value] as Map<String, Any>

        val impulseValue = waveParameters[DrawingParameter.IMPULSE.value].toString()
        addChooseValueModel(
            DrawingParameter.IMPULSE,
            R.string.drawing_tool_settings_title_impulse,
            impulseValue,
            Impulse.values().map { OptionItem(it.value, impulseValue == it.value) })

        val correctiveValue = waveParameters[DrawingParameter.CORRECTIVE.value].toString()
        addChooseValueModel(
            DrawingParameter.CORRECTIVE,
            R.string.drawing_tool_settings_title_corrective,
            correctiveValue,
            Corrective.values().map { OptionItem(it.value, impulseValue == it.value) })

        val decoration = waveParameters[DrawingParameter.DECORATION.value].toString()
        addChooseValueModel(
            DrawingParameter.DECORATION,
            R.string.drawing_tool_settings_title_decoration,
            decoration.capitalize(),
            Decoration.values().map { OptionItem(it.value, impulseValue == it.value) })

        val showLines =
            waveParameters[DrawingParameter.SHOW_LINES.value]?.toString()?.toBoolean() ?: false
        add(
            DrawingToolSettingsItem.Switch(
                R.string.drawing_tool_settings_title_show_lines,
                showLines,
                DrawingParameter.SHOW_LINES.value
            )
        )
    }

    class ViewModelFactory(
        private val argChartIQHandler: ChartIQ,
        private val drawingManager: DrawingManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(ChartIQ::class.java, DrawingManager::class.java)
                .newInstance(argChartIQHandler, drawingManager)
        }
    }
}
