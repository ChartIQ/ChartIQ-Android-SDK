package com.chartiq.demo.ui.signal.addsignal.add_condition

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentAddConditionBinding
import com.chartiq.demo.ui.chart.panel.settings.color.ChooseColorFragment
import com.chartiq.demo.ui.signal.addsignal.AddSignalViewModel

class AddConditionFragment : Fragment(), ChooseColorFragment.DialogFragmentListener {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val addSignalViewModel by activityViewModels<AddSignalViewModel>(factoryProducer = {
        AddSignalViewModel.ViewModelFactory(chartIQ, localizationManager, requireContext())
    })

    private val addConditionViewModel by viewModels<AddConditionViewModel>(factoryProducer = {
        AddConditionViewModel.ViewModelFactory()
    })

    private lateinit var binding: FragmentAddConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddConditionBinding.inflate(inflater, container, false)
        setupViews()
        setupViewModel()
        return binding.root
    }

    private fun setupViewModel() {
        with(addConditionViewModel) {
            currentColor.observe(viewLifecycleOwner) {
                (binding.colorImageView.background as GradientDrawable).setColor(it)
            }
            isShowIndicator2.observe(viewLifecycleOwner) {
                binding.indicator2TextInputLayout.isVisible = it
                binding.value2TextInputLayout.isVisible = false
            }
            isShowIndicator2Value.observe(viewLifecycleOwner) {
                binding.value2TextInputLayout.isVisible = it
            }
            isShowSaveSettings.observe(viewLifecycleOwner) {
                binding.shapeTextInputLayout.isVisible = it
                binding.sizeTextInputLayout.isVisible = it
                binding.positionTextInputLayout.isVisible = it
                binding.tagMarkTextInputLayout.isVisible = it
            }
            condition.observe(viewLifecycleOwner) { condition ->
                addSignalViewModel.addCondition(condition)
                findNavController().navigateUp()
            }
            isSaveAvailable.observe(viewLifecycleOwner) {
                binding.saveSignalButton.isEnabled = it
            }
        }
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            saveSignalButton.setOnClickListener {
                addConditionViewModel.onSaveCondition(
                    tagMarkEditText.text.toString()
                )
            }
            indicator1AutoCompleteTextView.apply {
                val list =
                    addSignalViewModel.selectedStudy.value!!.outputs?.map { it.key + " " + addSignalViewModel.selectedStudy.value!!.shortName }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list ?: emptyList()
                    )
                )
                list?.let {
                    addConditionViewModel.onSelectIndicator1(
                        it[0].substringBefore(addSignalViewModel.selectedStudy.value!!.shortName)
                    )
                }
                setText(list?.firstOrNull(), false)
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onSelectIndicator1(
                        text.toString()
                            .substringBefore(addSignalViewModel.selectedStudy.value!!.shortName)
                    )
                    filterSecondIndicator(text.toString())
                }
            }
            valueAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfValues.value!!.map {
                    localizationManager.getTranslationFromValue(
                        it.title,
                        requireContext()
                    )
                }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                )
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onValueSelected(list.indexOf(text.toString()))
                }
            }
            val item = indicator1AutoCompleteTextView.adapter.getItem(0)
            filterSecondIndicator(item.toString())

            colorLayout.setOnClickListener {
                ChooseColorFragment.getInstance(
                    "",
                    String.format("#%06X", 0xFFFFFF and addConditionViewModel.currentColor.value!!)
                ).apply {
                    setTargetFragment(
                        this@AddConditionFragment,
                        REQUEST_CODE_SHOW_COLOR_PICKER
                    )
                }.show(parentFragmentManager, ChooseColorFragment::class.simpleName)
            }

            markerTypeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfMarkerTypes.value!!.map { shape ->
                    localizationManager.getTranslationFromValue(
                        shape.title,
                        requireContext()
                    )
                }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                )
                setText(list.firstOrNull(), false)
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onMarkerTypeSelected(list.indexOf(text.toString()))
                }
            }

            shapeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfShapes.value!!.map { shape ->
                    localizationManager.getTranslationFromValue(
                        shape.title,
                        requireContext()
                    )
                }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                )
                setText(list.firstOrNull(), false)
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onShapeSelected(list.indexOf(text.toString()))
                }
            }
            sizeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfSizes.value!!.map { size ->
                    localizationManager.getTranslationFromValue(
                        size.title,
                        requireContext()
                    )
                }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                )
                setText(list.firstOrNull(), false)
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onSizeSelected(list.indexOf(text.toString()))
                }
            }
            positionAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfPositions.value!!.map { size ->
                    localizationManager.getTranslationFromValue(
                        size.title,
                        requireContext()
                    )
                }
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list
                    )
                )
                setText(list.firstOrNull(), false)
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onPositionSelected(list.indexOf(text.toString()))
                }
            }
            value2EditText.doOnTextChanged { text, start, before, count ->
                addConditionViewModel.onValue2Selected(text.toString())
            }
        }
    }

    private fun filterSecondIndicator(text: String) {
        with(binding) {
            indicator2AutoCompleteTextView.apply {
                val list = addSignalViewModel.selectedStudy.value!!.outputs?.map {
                    if (text.substringBefore(addSignalViewModel.selectedStudy.value!!.shortName)
                            .trim() != it.key
                    ) it.key + " " + addSignalViewModel.selectedStudy.value!!.shortName
                    else null
                }?.filterNotNull()?.toMutableList()
                list?.add(
                    localizationManager.getTranslationFromValue(
                        "value",
                        requireContext()
                    )
                )
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list?.toList()
                            ?: emptyList()
                    )
                )
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onSelectIndicator2(
                        text.toString()
                            .substringBefore(addSignalViewModel.selectedStudy.value!!.shortName)
                    )
                    if (text.toString() == localizationManager.getTranslationFromValue(
                            "value",
                            requireContext()
                        )
                    ) {
                        addConditionViewModel.onIndicator2ValueSelected()
                    } else {
                        addConditionViewModel.onIndicator2ValueUnSelected()
                    }
                }
            }
        }
    }

    override fun onChooseColor(parameter: String, color: Int) {
        addConditionViewModel.onChooseColor(color)
    }

    companion object {
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001
    }
}