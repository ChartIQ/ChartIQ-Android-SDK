package com.chartiq.demo.ui.settings.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chartiq.demo.databinding.FragmentChartLanguageSelectionBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.common.FullscreenDialogFragment
import com.chartiq.demo.ui.common.optionpicker.OptionItem
import com.chartiq.demo.ui.common.optionpicker.OptionsAdapter

class LanguageSelectionFragment : FullscreenDialogFragment() {
    private lateinit var binding: FragmentChartLanguageSelectionBinding

    private val selectedLanguage: ChartIQLanguage? by lazy {
        LanguageSelectionFragmentArgs.fromBundle(requireArguments()).selectedLanguage
    }
    private val languages: List<ChartIQLanguage> by lazy {
        ChartIQLanguage.values().toList()
    }
    private val optionsAdapter = OptionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartLanguageSelectionBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            optionsAdapter.apply {
                items = languages.map { OptionItem(it.title, selectedLanguage == it) }
                listener = OnSelectItemListener { option ->
                    val selectedLanguage = languages.first { it.title == option.value }
                    (targetFragment as DialogFragmentListener).onSelectLanguage(selectedLanguage)
                    dismiss()
                }
            }
            parametersRecyclerView.apply {
                adapter = optionsAdapter
                addItemDecoration(
                    LineItemDecoration.Default(
                        context = requireContext()
                    )
                )
                scrollToPosition(languages.indexOf(selectedLanguage))
            }
        }
    }

    companion object {
        fun getInstance(iqLanguage: ChartIQLanguage): LanguageSelectionFragment {
            return LanguageSelectionFragment().apply {
                arguments =
                    LanguageSelectionFragmentArgs.Builder(iqLanguage).build().toBundle()
            }
        }
    }

    interface DialogFragmentListener {
        fun onSelectLanguage(iqLanguage: ChartIQLanguage)
    }
}
