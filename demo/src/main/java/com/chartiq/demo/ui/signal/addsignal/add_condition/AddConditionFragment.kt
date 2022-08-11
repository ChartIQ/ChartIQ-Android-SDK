package com.chartiq.demo.ui.signal.addsignal.add_condition

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
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
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentAddConditionBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainViewModel
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
        AddConditionViewModel.ViewModelFactory(chartIQ, localizationManager, requireContext())
    })

    private val mainViewModel by activityViewModels<MainViewModel>(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    })

    private lateinit var hardcodedArray: Array<String>

    private lateinit var binding: FragmentAddConditionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hardcodedArray = resources.getStringArray(R.array.hardcoded_values)
        addSignalViewModel.selectedStudy.value?.let { addConditionViewModel.setStudy(it) }
        val args = AddConditionFragmentArgs.fromBundle(requireArguments())
        binding.toolbar.title = args.toolbarTitle
        setupViews()
        setupViewModel()
        addConditionViewModel.setColor(addSignalViewModel.defaultColor.value)
        addConditionViewModel.setCondition(args.conditionItem)
        addConditionViewModel.setSettingsVisibility(args.shouldShowSettings)
        setupViewsData()
        addConditionViewModel.setChartStyle(mainViewModel.chartStyle.value)
    }

    override fun onResume() {
        super.onResume()
        addConditionViewModel.checkColor()
    }

    private fun setupViewModel() {
        with(addConditionViewModel) {
            isAttentionVisible.observe(viewLifecycleOwner) { isShow ->
                binding.attentionLayout.isVisible = isShow
            }
            isShowAppearanceSettings.observe(viewLifecycleOwner) { isShow ->
                binding.appearanceSettings.isVisible = isShow
            }
            label.observe(viewLifecycleOwner) {
                binding.tagMarkEditText.setText(it)
            }
            this.currentColor.observe(viewLifecycleOwner) {
                (binding.colorImageView.background as GradientDrawable).setColor(it)
            }
            isShowRightIndicator.observe(viewLifecycleOwner) {
                binding.indicator2TextInputLayout.isVisible = it
                binding.value2TextInputLayout.isVisible = false
                addConditionViewModel.onSelectRightIndicator(binding.indicator2AutoCompleteTextView.text.toString())
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
                text?.length?.let { tagMarkEditText.setSelection(it) }
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
                val list = addConditionViewModel.listOfValues.value!!.map { operator ->
                    getString(
                        context.resources.getIdentifier(
                            operator.key,
                            "string",
                            requireContext().packageName
                        )
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
                    String.format(
                        "#%06X",
                        FORMAT_COLOR and addConditionViewModel.currentColor.value!!
                    )
                ).apply {
                    setTargetFragment(
                        this@AddConditionFragment,
                        REQUEST_CODE_SHOW_COLOR_PICKER
                    )
                }.show(parentFragmentManager, ChooseColorFragment::class.simpleName)
            }

            markerTypeAutoCompleteTextView.apply {
                val list = addConditionViewModel.listOfMarkerTypes.value!!.map { marker ->
                    getString(
                        context.resources.getIdentifier(
                            marker.key,
                            "string",
                            requireContext().packageName
                        )
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
                    getString(
                        context.resources.getIdentifier(
                            shape.key,
                            "string",
                            requireContext().packageName
                        )
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
                    getString(
                        context.resources.getIdentifier(
                            size.key,
                            "string",
                            requireContext().packageName
                        )
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
                val list = addConditionViewModel.listOfPositions.value!!.map { position ->
                    getString(
                        context.resources.getIdentifier(
                            position.key,
                            "string",
                            requireContext().packageName
                        )
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
                if (text.toString() == getString(R.string.value)) {
                    addConditionViewModel.onIndicator2ValueSelected()
                } else {
                    addConditionViewModel.onIndicator2ValueUnSelected()
                }
            }
            setText(list?.get(0) ?: "", false)
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
                    getString(
                        requireContext().resources.getIdentifier(
                            it.key, "string", requireContext().packageName
                        )
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
                getString(
                    requireContext().resources.getIdentifier(
                        addConditionViewModel.selectedMarker.value?.key ?: "",
                        "string",
                        requireContext().packageName
                    )
                ), false
            )
            sizeAutoCompleteTextView.setText(
                getString(
                    requireContext().resources.getIdentifier(
                        addConditionViewModel.selectedSignalSize.value?.key ?: "",
                        "string",
                        requireContext().packageName
                    )
                ), false
            )
            positionAutoCompleteTextView.setText(
                getString(
                    requireContext().resources.getIdentifier(
                        addConditionViewModel.selectedSignalPosition.value?.key ?: "",
                        "string",
                        requireContext().packageName
                    )
                ), false
            )
            shapeAutoCompleteTextView.setText(
                getString(
                    requireContext().resources.getIdentifier(
                        addConditionViewModel.selectedSignalShape.value?.key ?: "",
                        "string",
                        requireContext().packageName
                    )
                ), false
            )
        }
    }

    override fun onChooseColor(parameter: String, color: Int) {
        addConditionViewModel.onChooseColor(color)
    }

    companion object {
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001
        private const val FORMAT_COLOR = 0xFFFFFF
    }
}