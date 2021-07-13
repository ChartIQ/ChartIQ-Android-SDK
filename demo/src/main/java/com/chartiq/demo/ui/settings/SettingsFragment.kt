package com.chartiq.demo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentSettingsBinding
import com.chartiq.demo.ui.settings.chartstyle.ChartStyleSelectionFragment
import com.chartiq.demo.ui.settings.chartstyle.ChartTypeItem
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.demo.ui.settings.language.LanguageSelectionFragment

class SettingsFragment : Fragment(), ChartStyleSelectionFragment.DialogFragmentListener,
    LanguageSelectionFragment.DialogFragmentListener {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = {
        SettingsViewModel.ViewModelFactory(
            chartIQ,
            (requireActivity().application as ServiceLocator).applicationPreferences
        )
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
            chartConfigHeaderTextView.isVisible = BuildConfig.NEEDS_CHAR_STYLE_OPTION
            chartConfigContainer.isVisible = BuildConfig.NEEDS_CHAR_STYLE_OPTION
            toolbar.navigationIcon =
                if (BuildConfig.NEEDS_BACK_NAVIGATION) {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_back)
                } else {
                    null
                }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            chartConfigContainer.setOnClickListener {

                ChartStyleSelectionFragment
                    .getInstance(settingsViewModel.chartStyle.value).apply {
                        setTargetFragment(this@SettingsFragment, REQUEST_CODE_CHART_STYLE)
                    }
                    .show(parentFragmentManager, ChartStyleSelectionFragment::class.java.simpleName)
            }
            languageContainer.setOnClickListener {
                LanguageSelectionFragment.getInstance(settingsViewModel.language.value!!)
                    .apply {
                        setTargetFragment(this@SettingsFragment, REQUEST_CODE_LANGUAGE)
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
                chartConfigContainer.subtitle = getString(it.titleRes)
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


    override fun onSelect(chartStyle: ChartTypeItem) {
        settingsViewModel.updateChartStyle(chartStyle)
    }

    override fun onSelectLanguage(iqLanguage: ChartIQLanguage) {
        settingsViewModel.updateLanguage(iqLanguage)
    }

    companion object {
        private const val REQUEST_CODE_CHART_STYLE = 3333
        private const val REQUEST_CODE_LANGUAGE = 6666
    }

}
