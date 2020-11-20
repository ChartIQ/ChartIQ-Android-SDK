package com.chartiq.demo.ui.chart.panel.settings

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentDrawingToolSettingsBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.drawingtools.DrawingToolFragment.Companion.DEFAULT_TOOLS_LIST
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.settings.color.ChooseColorFragment
import com.chartiq.demo.ui.chart.panel.settings.line.ChooseLineFragment
import com.chartiq.demo.ui.chart.panel.settings.option.ChooseValueFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.sdk.ChartIQHandler
import com.chartiq.sdk.model.drawingtool.LineType
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager

class DrawingToolSettingsFragment : Fragment(),
    ChooseColorFragment.DialogFragmentListener,
    ChooseLineFragment.DialogFragmentListener,
    ChooseValueFragment.DialogFragmentListener {

    private lateinit var binding: FragmentDrawingToolSettingsBinding
    private val chartIQHandler: ChartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private val settingsViewModel: DrawingToolSettingsViewModel by viewModels(factoryProducer = {
        DrawingToolSettingsViewModel.ViewModelFactory(chartIQHandler, ChartIQDrawingManager())
    })
    private val settingsAdapter = SettingsAdapter()
    private val settingsListener = OnSelectItemListener<SettingsItem> { item ->
        when (item) {
            is SettingsItem.Switch -> updateBooleanParameter(item.param, item.checked)
            is SettingsItem.Color -> navigateToChooseColor(item.param, item.color)
            is SettingsItem.Deviation -> navigateToDeviationSettings(item)
            is SettingsItem.Style -> settingsViewModel.updateAnnotationParameters(
                item.weightParam,
                item.styleParam,
                item.isBold,
                item.isItalic
            )
            is SettingsItem.ChooseValue -> navigateToChooseValueFromList(
                item.param,
                item.valueList,
                item.isMultipleSelection
            )
            is SettingsItem.Line -> navigateToChooseLine(
                item.lineTypeParam,
                item.lineWidthParam,
                item.lineType,
                item.lineWidth
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrawingToolSettingsBinding.inflate(inflater, container, false)

        extractArguments()
        setupViews()
        return binding.root
    }

    override fun onChooseColor(parameter: String, color: Int) {
        settingsViewModel.updateColorParameter(parameter, color)
        settingsViewModel.refreshDrawingParameters()
    }

    override fun onChooseLine(
        lineParam: String,
        widthParam: String,
        lineType: LineType,
        lineWidth: Int
    ) {
        settingsViewModel.updateParameter(lineParam, lineType.value)
        settingsViewModel.updateParameter(widthParam, lineWidth.toString())
        settingsViewModel.refreshDrawingParameters()
    }

    override fun onChooseValue(
        parameter: String,
        valuesList: List<OptionItem>,
        isMultipleSelect: Boolean
    ) {
        val value = if (isMultipleSelect) {
            Base64.encodeToString(valuesList.toString().toByteArray(), Base64.DEFAULT)
        } else {
            valuesList.find { it.isSelected }!!.value
        }
        settingsViewModel.updateParameter(parameter, value)
    }

    private fun extractArguments() {
        arguments?.let { it ->
            val args = DrawingToolSettingsFragmentArgs.fromBundle(it)
            val drawingTool = args.argDrawingTool
            settingsViewModel.setDrawingTool(drawingTool)
            if (args.argDeviation != null) {
                val item = args.argDeviation
                settingsViewModel.setTitle(getString(item.title))
                settingsAdapter.items = item.settings
            } else {
                val item = DEFAULT_TOOLS_LIST.find { it.tool == drawingTool }!!
                settingsViewModel.setTitle(getString(item.nameRes))
                settingsViewModel.refreshDrawingParameters()
            }
        }
    }

    private fun setupViews() {
        with(binding) {
            settingsToolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                title = settingsViewModel.title.value
            }
            settingsAdapter.listener = settingsListener
            settingsRecyclerView.addItemDecoration(LineItemDecoration.Default(requireContext()))
            settingsRecyclerView.adapter = settingsAdapter
        }

        settingsViewModel.parameters.observe(viewLifecycleOwner) { params ->
            val list = settingsViewModel.setupList(params)

            // TODO: 19.11.20 Review the following construction
            val isNestedSettings = arguments?.let { it ->
                DrawingToolSettingsFragmentArgs.fromBundle(it).argDeviation != null
            } ?: false
            if (isNestedSettings) {
                list.find { it is SettingsItem.Deviation }?.let { deviation ->
                    deviation as SettingsItem.Deviation
                    settingsAdapter.items = deviation.settings
                }
            } else {
                settingsAdapter.items = list
            }
        }
    }

    private fun navigateToChooseLine(
        lineParam: String,
        widthParam: String,
        lineType: LineType,
        lineWidth: Int
    ) {
        val dialog = ChooseLineFragment.getInstance(lineParam, widthParam, lineType, lineWidth)
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_LINE_PICKER)
        dialog.show(parentFragmentManager, null)
    }

    private fun navigateToDeviationSettings(item: SettingsItem.Deviation) {
        val direction = DrawingToolSettingsFragmentDirections.actionDrawingToolSettingsFragmentSelf(
            settingsViewModel.drawingTool.value!!,
            item
        )
        findNavController().navigate(direction)
    }

    private fun navigateToChooseColor(param: String, selectedColor: String) {
        val dialog = ChooseColorFragment.getInstance(param, selectedColor)
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_COLOR_PICKER)
        dialog.show(parentFragmentManager, null)
    }

    private fun navigateToChooseValueFromList(
        param: String,
        valueList: List<OptionItem>,
        multipleSelection: Boolean
    ) {
        val dialog = ChooseValueFragment.getInstance(param, valueList, multipleSelection)
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_VALUE_LIST)
        dialog.show(parentFragmentManager, null)
    }

    private fun updateBooleanParameter(param: String, checked: Boolean) {
        settingsViewModel.updateSwitchParameter(param, checked)
    }

    companion object {
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001
        private const val REQUEST_CODE_SHOW_LINE_PICKER = 1002
        private const val REQUEST_CODE_SHOW_VALUE_LIST = 1003
    }
}
