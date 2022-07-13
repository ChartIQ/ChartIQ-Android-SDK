package com.chartiq.demo.ui.signal.addsignal.add_condition

import android.annotation.SuppressLint
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
import com.chartiq.demo.R
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

    private lateinit var hardcodedArray: Array<String>

    private lateinit var binding: FragmentAddConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddConditionBinding.inflate(inflater, container, false)
        hardcodedArray = resources.getStringArray(R.array.hardcoded_values)
        addSignalViewModel.selectedStudy.value?.let { addConditionViewModel.setStudy(it) }
        addConditionViewModel.setCondition(AddConditionFragmentArgs.fromBundle(requireArguments()).conditionItem)
        setupViewsData()
        setupViews()
        setupViewModel()
        return binding.root
    }

    private fun setupViewModel() {
        with(addConditionViewModel) {
            label.observe(viewLifecycleOwner) {
                binding.tagMarkEditText.setText(it)
            }
            currentColor.observe(viewLifecycleOwner) {
                (binding.colorImageView.background as GradientDrawable).setColor(it)
            }
            isShowRightIndicator.observe(viewLifecycleOwner) {
                binding.indicator2TextInputLayout.isVisible = it
                binding.value2TextInputLayout.isVisible = false
            }
            isShowRightValue.observe(viewLifecycleOwner) {
                binding.value2TextInputLayout.isVisible = it
            }
            isShowSaveSettings.observe(viewLifecycleOwner) {
                binding.shapeTextInputLayout.isVisible = it
                binding.sizeTextInputLayout.isVisible = it
                binding.positionTextInputLayout.isVisible = it
                binding.tagMarkTextInputLayout.isVisible = it
            }
            conditionItem.observe(viewLifecycleOwner) { condition ->
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
            tagMarkEditText.doOnTextChanged { text, start, before, count ->
                addConditionViewModel.onLabelChanged(text.toString())
            }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            saveSignalButton.setOnClickListener {
                addConditionViewModel.onSaveCondition(
                    tagMarkEditText.text.toString()
                )
            }
            colorImageView.background = (colorImageView.background as GradientDrawable).apply {
                setColor(
                    addConditionViewModel.currentColor.value!!
                )
            }
            indicator1AutoCompleteTextView.apply {
                val list =
                    addSignalViewModel.selectedStudy.value?.outputs?.map { it.key }
                list?.let {
                    addConditionViewModel.onSelectLeftIndicator(
                        it[0]
                    )
                }
                setText(list?.get(0) ?: "")
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list ?: emptyList()
                    )
                )
                doOnTextChanged { text, start, before, count ->
                    addConditionViewModel.onSelectLeftIndicator(text.toString())
                    filterSecondIndicator(text.toString())
                }
            }
            valueAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfValues.value!!.map {
                    localizationManager.getTranslationFromValue(
                        it.key,
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
            filterSecondIndicator(item as? String ?: "")

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
                        shape.key,
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
                    addConditionViewModel.onMarkerTypeSelected(list.indexOf(text.toString()))
                }
            }

            shapeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfShapes.value!!.map { shape ->
                    localizationManager.getTranslationFromValue(
                        shape.key,
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
                    addConditionViewModel.onShapeSelected(list.indexOf(text.toString()))
                }
            }
            sizeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfSizes.value!!.map { size ->
                    localizationManager.getTranslationFromValue(
                        size.key,
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
                    addConditionViewModel.onSizeSelected(list.indexOf(text.toString()))
                }
            }
            positionAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfPositions.value!!.map { size ->
                    localizationManager.getTranslationFromValue(
                        size.key,
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
                    addConditionViewModel.onPositionSelected(list.indexOf(text.toString()))
                }
            }
            value2EditText.doOnTextChanged { text, start, before, count ->
                addConditionViewModel.onRightValueSelected(text.toString())
            }
        }
    }

    private fun filterSecondIndicator(text: String) {
        binding.indicator2AutoCompleteTextView.apply {
            val list = addSignalViewModel.selectedStudy.value!!.outputs?.map {
                if (text != it.key) it.key
                else null
            }?.filterNotNull()?.toMutableList()
            list?.addAll(hardcodedArray)
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    list?.toList()
                        ?: emptyList()
                )
            )
            doOnTextChanged { text, start, before, count ->
                addConditionViewModel.onSelectRightIndicator(text.toString())
                if (text.toString() == localizationManager.getTranslationFromValue(
                        getString(R.string.value),
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

    @SuppressLint("SetTextI18n")
    fun setupViewsData() {
        with(binding) {
            addConditionViewModel.selectedLeftIndicator.value?.let {
                indicator1AutoCompleteTextView.setText(it, false)
            }
            addConditionViewModel.selectedOperator.value?.let {
                valueAutoCompleteTextView.setText(
                    localizationManager.getTranslationFromValue(
                        it.key,
                        requireContext()
                    ), false
                )
            }
            addConditionViewModel.selectedRightIndicator.value?.let {
                indicator2AutoCompleteTextView.setText(
                    it, false
                )
            }

            addConditionViewModel.selectedRightValue.value?.let {
                value2EditText.setText(it)
            }

            markerTypeAutoCompleteTextView.setText(
                localizationManager.getTranslationFromValue(
                    addConditionViewModel.selectedMarker.value?.key ?: "",
                    requireContext()
                )
            )
            sizeAutoCompleteTextView.setText(
                localizationManager.getTranslationFromValue(
                    addConditionViewModel.selectedSignalSize.value?.key ?: "",
                    requireContext()
                )
            )
            positionAutoCompleteTextView.setText(
                localizationManager.getTranslationFromValue(
                    addConditionViewModel.selectedSignalPosition.value?.key ?: "",
                    requireContext()
                )
            )
            shapeAutoCompleteTextView.setText(
                localizationManager.getTranslationFromValue(
                    addConditionViewModel.selectedSignalShape.value?.key ?: "",
                    requireContext()
                )
            )

        }
    }

    override fun onChooseColor(parameter: String, color: Int) {
        addConditionViewModel.onChooseColor(color)
    }

    companion object {
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001
    }
}