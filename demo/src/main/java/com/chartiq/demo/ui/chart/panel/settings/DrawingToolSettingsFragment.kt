package com.chartiq.demo.ui.chart.panel.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentDrawingToolSettingsBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.DrawingTools
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.settings.color.ChooseColorFragment
import com.chartiq.demo.ui.chart.panel.settings.line.ChooseLineFragment
import com.chartiq.demo.ui.chart.panel.settings.option.ChooseValueFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.sdk.ChartIQ
import com.chartiq.sdk.model.drawingtool.LineType
import com.chartiq.sdk.model.drawingtool.drawingmanager.ChartIQDrawingManager

class DrawingToolSettingsFragment : Fragment(),
    ChooseColorFragment.DialogFragmentListener,
    ChooseLineFragment.DialogFragmentListener,
    ChooseValueFragment.DialogFragmentListener {

    private lateinit var binding: FragmentDrawingToolSettingsBinding
    private val chartIQ: ChartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val settingsViewModel: DrawingToolSettingsViewModel by activityViewModels(
        factoryProducer = {
            DrawingToolSettingsViewModel.ViewModelFactory(chartIQ, ChartIQDrawingManager())
        })
    private val settingsAdapter = DrawingToolSettingsAdapter()
    private val settingsListener = OnSelectItemListener<DrawingToolSettingsItem> { item ->
        when (item) {
            is DrawingToolSettingsItem.Switch -> updateBooleanParameter(item)
            is DrawingToolSettingsItem.Color -> navigateToChooseColor(item)
            is DrawingToolSettingsItem.Deviation -> navigateToDeviationSettings(item)
            is DrawingToolSettingsItem.Style -> settingsViewModel.updateAnnotationParameters(item)
            is DrawingToolSettingsItem.ChooseValue -> navigateToChooseValueFromList(item)
            is DrawingToolSettingsItem.Line -> navigateToChooseLine(item)
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

    override fun onChooseValue(parameter: String, valuesList: List<OptionItem>) {
        settingsViewModel.updateChooseValueParameter(parameter, valuesList)
    }

    private fun extractArguments() {
        val args = DrawingToolSettingsFragmentArgs.fromBundle(requireArguments())
        val drawingTool = args.argDrawingTool
        settingsViewModel.setDrawingTool(drawingTool)
        if (args.argDeviation != null) {
            val item = args.argDeviation
            binding.settingsToolbar.title = getString(item!!.title)
        } else {
            val item = DrawingTools.values().find { it.tool == drawingTool } ?: return
            binding.settingsToolbar.title = getString(item.nameRes)
            settingsViewModel.refreshDrawingParameters()
        }
    }

    private fun setupViews() {
        val isDeviation = DrawingToolSettingsFragmentArgs.fromBundle(requireArguments()).argDeviation != null
        with(binding) {
            settingsToolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }
            settingsAdapter.listener = settingsListener

            val decoration = if (isDeviation) {
                DeviationItemDecoration(requireContext())
            } else {
                LineItemDecoration.Default(requireContext())
            }
            settingsRecyclerView.addItemDecoration(decoration)
            settingsRecyclerView.adapter = settingsAdapter
        }

        settingsViewModel.parameters.observe(viewLifecycleOwner) { event ->
            if (isDeviation) {
                settingsViewModel.setupList(event.peekContent(), true)
            }
            event.getContentIfNotHandled()?.let { params ->
                settingsViewModel.setupList(params, false)
            }
        }
        settingsViewModel.settingsList.observe(viewLifecycleOwner) { list ->
            settingsAdapter.items = list
        }
    }

    private fun navigateToChooseLine(item: DrawingToolSettingsItem.Line) {
        val dialog = ChooseLineFragment.getInstance(
            item.lineTypeParam,
            item.lineWidthParam,
            item.lineType,
            item.lineWidth
        )
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_LINE_PICKER)
        dialog.show(parentFragmentManager, null)
    }

    private fun navigateToDeviationSettings(item: DrawingToolSettingsItem.Deviation) {
        val direction = DrawingToolSettingsFragmentDirections
            .actionDrawingToolSettingsFragmentSelf(settingsViewModel.drawingTool.value!!)
            .setArgDeviation(item)
        findNavController().navigate(direction)
    }

    private fun navigateToChooseColor(item: DrawingToolSettingsItem.Color) {
        val dialog = ChooseColorFragment.getInstance(item.param, item.color)
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_COLOR_PICKER)
        dialog.show(parentFragmentManager, null)
    }

    private fun navigateToChooseValueFromList(item: DrawingToolSettingsItem.ChooseValue) {
        val dialog = ChooseValueFragment.getInstance(
            item.title,
            item.param,
            item.valueList,
            item.isMultipleSelection,
            item.hasCustomValueSupport
        )
        dialog.setTargetFragment(this, REQUEST_CODE_SHOW_VALUE_LIST)
        dialog.show(parentFragmentManager, null)
    }

    private fun updateBooleanParameter(item: DrawingToolSettingsItem.Switch) {
        settingsViewModel.updateSwitchParameter(item.param, item.checked)
    }

    companion object {
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001
        private const val REQUEST_CODE_SHOW_LINE_PICKER = 1002
        private const val REQUEST_CODE_SHOW_VALUE_LIST = 1003
    }
}
