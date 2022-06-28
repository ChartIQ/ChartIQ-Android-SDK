package com.chartiq.demo.ui.signal.addsignal

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentAddSignalBinding
import com.chartiq.demo.ui.signal.OnBackPressed
import com.chartiq.demo.ui.signal.addsignal.add_condition.ConditionItemTouchCallBack
import com.chartiq.demo.ui.study.studydetails.ActiveStudyDetailsFragmentArgs
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.SignalJoiner
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
    }

    private fun setupViewModel() {
        with(addSignalViewModel) {
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
                binding.addConditionButton.isEnabled = isAvailable
            }
            listOfConditions.observe(viewLifecycleOwner) { list ->
                processConditionsList(list)
            }
        }
    }

    private fun processConditionsList(list: List<Condition>) {
        conditionAdapter.items = list.mapIndexed { index, condition ->
            ConditionItem(
                condition,
                getString(R.string.signal_condition_n, index + 1),
                "Jaw Alligator Crosses Above Teeth Alligator"
            )
        }
    }

    private fun setupViews() {
        with(binding) {
            signalNameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    addSignalViewModel.onNameChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })

            signalDescriptionEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    addSignalViewModel.onDescriptionChanged(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
            conditionsRecyclerView.apply {
                isNestedScrollingEnabled = false
                adapter = conditionAdapter.apply {
                    signalJoiner = addSignalViewModel.signalJoiner.value!!
                    listener = object : ConditionsClickListener {
                        override fun onClick(condition: Condition) {

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
                    AddSignalFragmentDirections.actionAddSignalFragmentToAddConditionSignalFragment()
                )
            }
            settingsButton.setOnClickListener {
                val bundle =
                    ActiveStudyDetailsFragmentArgs.Builder(addSignalViewModel.selectedStudy.value!!)
                        .build().toBundle()
                findNavController().navigate(R.id.activeStudyDetailsFragment, bundle)
            }
            saveSignalButton.setOnClickListener {
                addSignalViewModel.saveSignal()
                addSignalViewModel.clearViewModel()
                findNavController().navigateUp()
            }
        }
    }

    override fun onBackPressed() {
        addSignalViewModel.onBackPressed()
        findNavController().navigateUp()
    }
}