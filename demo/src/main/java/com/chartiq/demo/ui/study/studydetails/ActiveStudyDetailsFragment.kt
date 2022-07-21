package com.chartiq.demo.ui.study.studydetails

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyDetailsBinding
import com.chartiq.demo.ui.chart.panel.settings.color.ChooseColorFragment
import com.chartiq.demo.ui.study.parameterselect.SelectParameterDialogFragment
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import kotlinx.android.synthetic.main.fragment_study_details.*

class ActiveStudyDetailsFragment : Fragment(), SelectParameterDialogFragment.DialogFragmentListener,
    ChooseColorFragment.DialogFragmentListener {

    private val study: Study by lazy {
        ActiveStudyDetailsFragmentArgs.fromBundle(requireArguments()).study
    }
    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val viewModel: ActiveStudyDetailsViewModel by viewModels(factoryProducer = {
        ActiveStudyDetailsViewModel.ViewModelFactory(chartIQ, study)
    })
    private val studyDetailsAdapter = StudyDetailsAdapter()

    private lateinit var binding: FragmentStudyDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStudyDetailsBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.apply {
                title =
                    localizationManager.getTranslationFromValue(study.type ?: "", requireContext())
                if (ActiveStudyDetailsFragmentArgs.fromBundle(requireArguments()).showSettings) {
                    menu.findItem(R.id.action_clone_details).setOnMenuItemClickListener {
                        viewModel.cloneStudy()
                        findNavController().navigateUp()
                        true
                    }
                    menu.findItem(R.id.action_reset_details).setOnMenuItemClickListener {
                        showResetDialog()
                        true
                    }
                    menu.findItem(R.id.action_delete_details).setOnMenuItemClickListener {
                        showDeleteDialog()
                        true
                    }
                } else {
                    for (i in 0 until toolbar.menu.size) {
                        menu.getItem(i).isVisible = false
                    }
                }
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            detailsRecyclerView.apply {
                adapter = studyDetailsAdapter.apply {
                    listener = object : StudyDetailsAdapter.StudyParameterListener {
                        override fun onCheckboxParamChange(
                            parameter: StudyParameter.Checkbox,
                            isChecked: Boolean
                        ) {
                            viewModel.onCheckboxParamChange(parameter, isChecked)
                        }

                        override fun onTextParamChange(
                            parameter: StudyParameter,
                            newValue: String
                        ) {
                            viewModel.onTextParamChange(parameter, newValue)
                            Log.i(TAG, "onTextParamChange $newValue")

                        }

                        override fun onNumberParamChange(
                            parameter: StudyParameter.Number,
                            newValue: Double?
                        ) {
                            viewModel.onNumberParamChange(parameter, newValue)
                            Log.i(TAG, "onNumberParamChange $newValue")

                        }

                        override fun onColorParamChange(studyParameter: StudyParameter) {
                            if (studyParameter is StudyParameter.Color) {
                                ChooseColorFragment.getInstance(
                                    studyParameter.name,
                                    studyParameter.value
                                ).apply {
                                    setTargetFragment(
                                        this@ActiveStudyDetailsFragment,
                                        REQUEST_CODE_SHOW_COLOR_PICKER
                                    )
                                }.show(parentFragmentManager, ChooseColorFragment::class.simpleName)

                            } else if (studyParameter is StudyParameter.TextColor) {
                                ChooseColorFragment.getInstance(
                                    studyParameter.name,
                                    studyParameter.color ?: ""
                                ).apply {
                                    setTargetFragment(
                                        this@ActiveStudyDetailsFragment,
                                        REQUEST_CODE_SHOW_COLOR_PICKER
                                    )
                                }
                                    .show(
                                        parentFragmentManager,
                                        ChooseColorFragment::class.simpleName
                                    )
                            }
                        }

                        override fun onSelectParamChange(studyParameter: StudyParameter.Select) {
                            SelectParameterDialogFragment.getInstance(studyParameter).apply {
                                setTargetFragment(
                                    this@ActiveStudyDetailsFragment,
                                    REQUEST_CODE_SHOW_OPTIONS_SELECTOR
                                )
                            }.show(
                                parentFragmentManager,
                                SelectParameterDialogFragment::class.java.simpleName
                            )
                        }
                    }
                }
                addItemDecoration(StudyDetailsItemDecorator(requireContext()))
            }
            saveStudyButton.setOnClickListener { viewModel.updateStudy() }
        }
        viewModel.studyParams.observe(viewLifecycleOwner) {
            studyDetailsAdapter.items = it
        }
        viewModel.successUpdateEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                hideKeyboard()
                findNavController().navigateUp()
            }
        }
        viewModel.errorList.observe(viewLifecycleOwner) { errorSet ->
            saveStudyButton.isEnabled = errorSet.isEmpty()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext(), R.style.NegativeAlertDialogTheme)
            .setTitle(getString(R.string.study_details_remove_alert_title))
            .setMessage(getString(R.string.study_details_remove_alert_message))
            .setPositiveButton(getString(R.string.study_details_remove)) { _, _ ->
                viewModel.deleteStudy()
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.study_details_cancel), null)
            .show()
    }

    private fun showResetDialog() {
        AlertDialog.Builder(requireContext(), R.style.PositiveAlertDialogTheme)
            .setTitle(getString(R.string.study_details_reset_alert_title))
            .setMessage(getString(R.string.study_details_reset_alert_message))
            .setPositiveButton(getString(R.string.study_details_reset)) { _, _ ->
                viewModel.resetStudy()
            }
            .setNegativeButton(getString(R.string.study_details_cancel), null)
            .show()
    }

    private fun hideKeyboard() {
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {
        private val TAG = ActiveStudyDetailsFragment::class.java.simpleName
        private const val REQUEST_CODE_SHOW_OPTIONS_SELECTOR = 102
        private const val REQUEST_CODE_SHOW_COLOR_PICKER = 1001


    }

    override fun onSelectOption(parameter: StudyParameter.Select, newValue: String) {
        viewModel.onSelectChange(parameter, newValue)
    }

    override fun onChooseColor(parameter: String, color: Int) {
        viewModel.onSelectColor(parameter, color)
    }
}
