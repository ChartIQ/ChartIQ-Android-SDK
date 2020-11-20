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
    val title = MutableLiveData<String>()

    fun setupList(params: Map<String, Any>): List<SettingsItem> {
        val list = mutableListOf<SettingsItem>()
        val tool = drawingTool.value!!

        with(drawingManager) {
            if (isSupportingFillColor(tool)) {
                addFillColorModel(params, list)
            }
            if (isSupportingLineColor(tool)) {
                addLineColorModel(params, list)
            }
            if (isSupportingLineType(tool)) {
                addLineTypeModels(params, list)
            }
            if (isSupportingFont(tool)) {
                addFontModels(params, list)
            }
            if (isSupportingAxisLabel(tool)) {
                addAxisLabelModels(params, list)
            }
            if (isSupportingDeviations(tool)) {
                addDeviationModels(params, list)
            }
            if (isSupportingFibonacci(tool)) {
                addFibonacciModels(params, list)
            }
            if (isSupportingElliottWave(tool)) {
                addElliotWaveModels(params, list)
            }
        }
        return list
    }

    fun refreshDrawingParameters() {
        chartIQHandler.getDrawingParameters(drawingTool.value!!) { params ->
            parameters.value = params
        }
    }

    fun updateAnnotationParameters(
        weightParam: String,
        styleParam: String,
        isBold: Boolean,
        isItalic: Boolean
    ) {
        val boldValue = if (isBold) {
            DrawingParameter.BOLD.value
        } else {
            DrawingParameter.BOLD_OFF.value
        }
        val italicValue = if (isItalic) {
            DrawingParameter.ITALIC.value
        } else {
            DrawingParameter.NORMAL.value
        }
        updateParameter(weightParam, boldValue)
        updateParameter(styleParam, italicValue)
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

    fun setTitle(title: String) {
        this.title.value = title
    }

    fun setDrawingTool(drawingTool: DrawingTool) {
        this.drawingTool.value = drawingTool
    }

    private fun addFillColorModel(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val colorParam = DrawingParameter.FILL_COLOR.value
        val color = params[colorParam].toString()
        list.add(
            SettingsItem.Color(
                R.string.drawing_tool_settings_title_fill_color,
                color,
                colorParam
            )
        )
    }

    private fun addLineColorModel(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val colorParam = DrawingParameter.LINE_COLOR.value
        val color = params[colorParam].toString()
        list.add(
            SettingsItem.Color(
                R.string.drawing_tool_settings_title_line_color,
                color,
                colorParam
            )
        )
    }

    private fun addLineTypeModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val lineType = params[DrawingParameter.LINE_TYPE.value].toString()
        val lineWidth = params[DrawingParameter.LINE_WIDTH.value].toString()
        list.add(
            SettingsItem.Line(
                R.string.drawing_tool_settings_title_line_type,
                LineType.valueOf(lineType.toUpperCase(Locale.getDefault())),
                lineWidth.toDouble().toInt()
            )
        )
    }

    // TODO: 17.11.20 Discuss with Yuliya if it would be better to use Font object in SDK instead
    private fun addFontModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val font = params[DrawingParameter.FONT.value] as Map<String, String>

        // family
        val familyParam = DrawingParameter.FAMILY.value
        val family = font[familyParam].toString()
        list.add(
            SettingsItem.ChooseValue(
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
        val styleParam = DrawingParameter.STYLE.value
        val style = font[styleParam].toString()
        val isItalic = style == DrawingParameter.ITALIC.value

        // is text bold
        val weightParam = DrawingParameter.WEIGHT.value
        // if value equals `300` it's normal, but it can also be string `bold`
        val isBold: Boolean = try {
            val weight = font[weightParam]?.toInt()
            !(weight != null && weight <= DrawingParameter.BOLD_OFF.value.toInt())
        } catch (e: NumberFormatException) {
            font[weightParam] == DrawingParameter.BOLD.value
        }

        list.add(
            SettingsItem.Style(
                R.string.drawing_tool_settings_title_font_style,
                isBold,
                isItalic
            )
        )

        // size
        val sizeParam = DrawingParameter.SIZE.value
        val size = font[sizeParam].toString()
        list.add(
            SettingsItem.ChooseValue(
                R.string.drawing_tool_settings_title_font_size,
                size,
                sizeParam,
                FontSize.values().map {
                    OptionItem(it.pxValue, it.pxValue == size)
                }
            )
        )
    }

    private fun addAxisLabelModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val checked = params[DrawingParameter.AXIS_LABEL.value]?.toString()?.toBoolean() ?: false
        list.add(
            SettingsItem.Switch(
                R.string.drawing_tool_settings_title_axis_label,
                checked,
                DrawingParameter.AXIS_LABEL.value
            )
        )
    }

    private fun addDeviationModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val deviationList = mutableListOf<SettingsItem>()
        fun addColorModel(colorParam: DrawingParameter, @StringRes title: Int) {
            val color = params[colorParam.value].toString()
            deviationList.add(
                SettingsItem.Color(
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
                SettingsItem.Switch(
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
                SettingsItem.Line(
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
        list.add(
            SettingsItem.Deviation(
                R.string.drawing_tool_settings_title_deviations_settings,
                deviationList
            )
        )
    }

    private fun addFibonacciModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        val fibsParam = DrawingParameter.FIBS.value
        val values = Gson().fromJson(params[fibsParam].toString(), Array<Fib>::class.java).toList()
        list.add(
            SettingsItem.ChooseValue(
                R.string.drawing_tool_settings_title_config,
                "",
                fibsParam,
                values.map {
                    val value = ((it.level * 100).round(3)).toString()
                    OptionItem(value, it.display)
                },
                true
            ))
    }

    private fun addElliotWaveModels(params: Map<String, Any>, list: MutableList<SettingsItem>) {
        fun addChooseValueModel(
            param: DrawingParameter,
            @StringRes title: Int,
            secondaryText: String,
            values: List<OptionItem>
        ) {
            list.add(
                SettingsItem.ChooseValue(
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
        list.add(
            SettingsItem.Switch(
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
