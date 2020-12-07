package com.chartiq.demo.ui.settings.language

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentChartLanguageSelectionBinding
import com.chartiq.demo.ui.LineItemDecoration

class LanguageSelectionFragment : DialogFragment() {
    private lateinit var binding: FragmentChartLanguageSelectionBinding

    private val selectedLanguage: ChartIQLanguage? by lazy {
        LanguageSelectionFragmentArgs.fromBundle(requireArguments()).selectedLanguage
    }
    private val languages: List<ChartIQLanguage> by lazy {
        ChartIQLanguage.values().toList()
    }
    private val optionsAdapter = SelectLanguageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartLanguageSelectionBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.attributes?.windowAnimations = R.style.FullScreenDialog
        }
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            optionsAdapter.apply {
                items = languages
                selectedValue = selectedLanguage
                listener = object : SelectLanguageAdapter.SelectLanguageListener {
                    override fun onSelectLanguage(selectedValue: ChartIQLanguage) {
                        (targetFragment as DialogFragmentListener).onSelectLanguage(selectedValue)
                        dismiss()
                    }
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
        fun onSelectLanguage(chartStyle: ChartIQLanguage)
    }
}
