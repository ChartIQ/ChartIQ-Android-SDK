package com.chartiq.demo.ui.signal.addsignal

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentAddSignalBinding
import com.chartiq.demo.ui.OnBackPressed
import com.chartiq.demo.ui.signal.addsignal.add_condition.ConditionItemTouchCallBack
import com.chartiq.demo.ui.study.studydetails.ActiveStudyDetailsFragmentArgs
import com.chartiq.sdk.model.signal.SignalJoiner
import com.chartiq.sdk.model.study.StudySimplified
import java.math.BigDecimal
import java.util.*

class AddSignalFragment : Fragment(), OnBackPressed {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }

    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }

    private val conditionAdapter = ConditionsAdapter()

    private val addSignalViewModel: AddSignalViewModel by activityViewModels(factoryProducer = {
        AddSignalViewModel.ViewModelFactory(chartIQ, localizationManager, requireContext())
    })

    private lateinit var binding: FragmentAddSignalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSignalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupViews()
        AddSignalFragmentArgs.fromBundle(requireArguments()).signal?.let {
            addSignalViewModel.setSignal(it)
        }
        setFragmentResultListener(SETTINGS_KEY) { requestKey, bundle ->
            val study = bundle.get(SETTINGS_STUDY_KEY) as? StudySimplified
            addSignalViewModel.onStudyEdited(study)
        }
    }

    override fun onResume() {
        super.onResume()
        addSignalViewModel.checkColor()
    }

    private fun setupViewModel() {
        with(addSignalViewModel) {
            signalJoiner.observe(viewLifecycleOwner) { joiner ->
                conditionAdapter.signalJoiner = joiner
            }
            editType.observe(viewLifecycleOwner) { type ->
                val titleId = when (type) {
                    SignalEditType.NEW_SIGNAL -> R.string.signal_title_add_signal
                    SignalEditType.EDIT_SIGNAL -> R.string.signal_title_edit_signal
                }
                if (type == SignalEditType.EDIT_SIGNAL) {
                    binding.editStudyButton.isVisible = false
                }
                binding.toolbar.setTitle(titleId)
            }
            selectedStudy.observe(viewLifecycleOwner) { study ->
                if (study == null) {
                    binding.selectStudyLayout.isVisible = true
                    binding.editStudyLayout.isVisible = false
                } else {
                    binding.selectedStudyText.text =
                        localizationManager.getTranslationFromValue(study.name, requireContext())
                    binding.selectStudyLayout.isVisible = false
                    binding.editStudyLayout.isVisible = true
                }
            }
            isSaveAvailable.observe(viewLifecycleOwner) { isAvailable ->
                binding.saveSignalButton.isEnabled = isAvailable
            }
            isAddConditionAvailable.observe(viewLifecycleOwner) { isAvailable ->
                binding.addConditionButton.isVisible = isAvailable
            }
            listOfConditions.observe(viewLifecycleOwner) { list ->
                processConditionsList(list)
            }
            name.observe(viewLifecycleOwner) {
                binding.signalNameEditText.setText(it)
            }
            description.observe(viewLifecycleOwner) {
                binding.signalDescriptionEditText.setText(it)
            }
        }
    }

    private fun processConditionsList(list: List<ConditionItem>) {
        conditionAdapter.items = list.mapIndexed { index, conditionItem ->
            val rightIndicator = try {
                val number = BigDecimal(conditionItem.condition.rightIndicator ?: "")
                number.toPlainString()
            } catch (e: Exception) {
                (conditionItem.condition.rightIndicator ?: "").substringBefore(
                    addSignalViewModel.selectedStudy.value!!.shortName
                )
            }
            val description =
                conditionItem.condition.leftIndicator.substringBefore(addSignalViewModel.selectedStudy.value!!.shortName) + getString(
                    requireContext().resources.getIdentifier(
                        conditionItem.condition.signalOperator.key,
                        "string",
                        requireContext().packageName
                    )
                ) + " " + rightIndicator
            ConditionItem(
                condition = conditionItem.condition,
                title = getString(R.string.signal_condition_n, index + 1),
                description = description,
                displayedColor = conditionItem.displayedColor,
                UUID = conditionItem.UUID
            )
        }
    }

    private fun setupViews() {
        with(binding) {
            signalNameEditText.doOnTextChanged { text, start, before, count ->
                addSignalViewModel.onNameChanged(text.toString())
                text?.length?.let { signalNameEditText.setSelection(it) }
            }

            signalDescriptionEditText.doOnTextChanged { text, start, before, count ->
                addSignalViewModel.onDescriptionChanged(text.toString())
                text?.length?.let { signalDescriptionEditText.setSelection(it) }
            }
            conditionsRecyclerView.apply {
                isNestedScrollingEnabled = false
                adapter = conditionAdapter.apply {
                    signalJoiner = addSignalViewModel.signalJoiner.value!!
                    listener = object : ConditionsClickListener {
                        override fun onClick(
                            condition: ConditionItem,
                            signalJoiner: SignalJoiner,
                            isFirstItem: Boolean
                        ) {
                            findNavController().navigate(
                                AddSignalFragmentDirections.actionAddSignalFragmentToAddConditionSignalFragment(
                                    condition,
                                    condition.title,
                                    signalJoiner == SignalJoiner.OR || isFirstItem
                                )
                            )
                        }

                        override fun onChangeJoiner(signalJoiner: SignalJoiner) {
                            addSignalViewModel.onJoinerChanged(signalJoiner)
                        }

                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(
                    ConditionItemTouchCallBack(
                        getString(R.string.study_delete).uppercase(Locale.getDefault()),
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.coralRed))
                    ).apply {
                        onSwipeListener =
                            ConditionItemTouchCallBack.OnSwipeListener { viewHolder, _ ->
                                val position = viewHolder.adapterPosition
                                val conditionItem = conditionAdapter.items[position / 2]
                                addSignalViewModel.deleteCondition(conditionItem.condition)
                            }
                    }
                )
                deleteItemTouchHelper.attachToRecyclerView(this)
            }
            toolbar.setNavigationOnClickListener {
                addSignalViewModel.onBackPressed()
                findNavController().navigateUp()
            }
            selectStudyButton.setOnClickListener {
                findNavController().navigate(
                    AddSignalFragmentDirections.actionAddSignalFragmentToSelectStudyForSignalFragment()
                )
            }
            editStudyButton.setOnClickListener {
                findNavController().navigate(
                    AddSignalFragmentDirections.actionAddSignalFragmentToSelectStudyForSignalFragment()
                )
            }
            addConditionButton.setOnClickListener {
                findNavController().navigate(
                    AddSignalFragmentDirections.actionAddSignalFragmentToAddConditionSignalFragment(
                        null,
                        getString(
                            R.string.signal_condition_n,
                            (addSignalViewModel.listOfConditions.value ?: emptyList()).size + 1
                        ),
                        addSignalViewModel.signalJoiner.value == SignalJoiner.OR
                                || (addSignalViewModel.listOfConditions.value
                            ?: emptyList()).isEmpty()
                    )
                )
            }
            settingsButton.setOnClickListener {
                val bundle =
                    ActiveStudyDetailsFragmentArgs.Builder(
                        addSignalViewModel.selectedStudy.value!!,
                        false
                    )
                        .build().toBundle()
                findNavController().navigate(R.id.activeStudyDetailsFragment, bundle)
            }
            saveSignalButton.setOnClickListener {
                addSignalViewModel.saveSignal()
                findNavController().navigateUp()
            }
        }
    }

    override fun onBackPressed() {
        addSignalViewModel.onBackPressed()
        findNavController().navigateUp()
    }

    companion object {
        const val SETTINGS_KEY = "study settings"
        const val SETTINGS_STUDY_KEY = "study key"
    }
}