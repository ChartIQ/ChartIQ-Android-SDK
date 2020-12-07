package com.chartiq.demo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentSettingsBinding
import com.chartiq.demo.ui.settings.chartstyle.ChartStyleSelectionFragment
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeModel

class SettingsFragment : Fragment(), ChartStyleSelectionFragment.DialogFragmentListener {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = {
        SettingsViewModel.ViewModelFactory(chartIQ)
    })
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            chartConfigContainer.setOnClickListener {

                ChartStyleSelectionFragment
                    .getInstance(settingsViewModel.chartStyle.value).apply {
                        setTargetFragment(this@SettingsFragment, REQUEST_CODE)
                    }
                    .show(parentFragmentManager, ChartStyleSelectionFragment::class.java.simpleName)
            }
            languageContainer.setOnClickListener {
                // todo open language screen
            }
            logScaleLayout.onCheckChangeListener = {
                settingsViewModel.changeLogScale(it)
            }
            invertYLayout.onCheckChangeListener = {
                settingsViewModel.changeInvertY(it)
            }
            extendHoursLayout.onCheckChangeListener = {
                settingsViewModel.changeExtendHours(it)
            }
            settingsViewModel.chartStyle.observe(viewLifecycleOwner) {
                chartConfigContainer.subtitle = it.title
            }
            settingsViewModel.logScale.observe(viewLifecycleOwner) {
                logScaleLayout.isChecked = it

            }
            settingsViewModel.invertYAxis.observe(viewLifecycleOwner) {
                invertYLayout.isChecked = it
            }
            settingsViewModel.extendHours.observe(viewLifecycleOwner) {
                extendHoursLayout.isChecked = it
            }
        }
    }

    override fun onSelect(chartStyle: ChartTypeModel) {
        settingsViewModel.updateChartStyle(chartStyle)
    }

    companion object {
        private const val REQUEST_CODE = 3333
    }
}
