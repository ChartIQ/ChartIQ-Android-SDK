package com.chartiq.demo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentSettingsBinding
import com.chartiq.demo.ui.settings.chartstyle.ChartStyleSelectionFragment
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeModel
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.demo.ui.settings.language.LanguageSelectionFragment

class SettingsFragment : Fragment(), ChartStyleSelectionFragment.DialogFragmentListener,
    LanguageSelectionFragment.DialogFragmentListener {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = {
        SettingsViewModel.ViewModelFactory(chartIQ, ApplicationPrefs.Default(requireContext()))
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
                        setTargetFragment(this@SettingsFragment, CHART_STYLE_REQUEST_CODE)
                    }
                    .show(parentFragmentManager, ChartStyleSelectionFragment::class.java.simpleName)
            }
            languageContainer.setOnClickListener {
                LanguageSelectionFragment.getInstance(settingsViewModel.language.value!!)
                    .apply {
                        setTargetFragment(this@SettingsFragment, LANGUAGE_REQUEST_CODE)
                    }
                    .show(parentFragmentManager, LanguageSelectionFragment::class.java.simpleName)
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
            settingsViewModel.language.observe(viewLifecycleOwner) {
                languageContainer.subtitle = it.title
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

    override fun onChartStyleSelect(chartStyle: ChartTypeModel) {
        settingsViewModel.updateChartStyle(chartStyle)
    }

    override fun onSelectLanguage(iqLanguage: ChartIQLanguage) {
        settingsViewModel.updateLanguage(iqLanguage)
    }

    companion object {
        private const val CHART_STYLE_REQUEST_CODE = 3333
        private const val LANGUAGE_REQUEST_CODE = 6666
    }

}
