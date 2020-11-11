package com.chartiq.demo.ui.study.studydetails

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyDetailsBinding
import com.chartiq.sdk.model.Study
import com.chartiq.sdk.model.StudyParameter

class ActiveStudyDetailsFragment : Fragment() {

    private val study: Study by lazy {
        ActiveStudyDetailsFragmentArgs.fromBundle(requireArguments()).study
    }
    private val chartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }
    private val viewModel: ActiveStudyDetailsViewModel by viewModels(factoryProducer = {
        ActiveStudyDetailsViewModel.ViewModelFactory(chartIQHandler, study)
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
                title = study.type
                menu.findItem(R.id.action_clone_details).setOnMenuItemClickListener {
                    viewModel.cloneStudy()
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
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            detailsRecyclerView.apply {
                adapter = studyDetailsAdapter.apply {
                    listener = object : StudyDetailsAdapter.StudyParameterListener {
                        override fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, isChecked: Boolean) {
                            viewModel.onCheckboxParamChange(parameter, isChecked)
                            Log.i(TAG, "onCheckboxParamChange")
                        }

                        override fun onTextParamChange(parameter: StudyParameter, newValue: String) {
                            viewModel.onTextParamChange(parameter, newValue)
                            Log.i(TAG, "onTextParamChange newValue $newValue")
                        }

                        override fun onNumberParamChange(parameter: StudyParameter.Number, newValue: Double) {
                            viewModel.onNumberParamChange(parameter, newValue)
                            Log.i(TAG, "onNumberParamChange newValue $newValue")
                        }

                        override fun onColorParamChange(studyParameter: StudyParameter) {
                            //todo open color picker
                            Log.i(TAG, "onColorParamChange")
                        }

                        override fun onSelectParamChange(studyParameter: StudyParameter.Select) {
                            //todo open select picker
                            Log.i(TAG, "onSelectParamChange")
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
        viewModel.canUpdateParameters.observe(viewLifecycleOwner) {
            binding.saveStudyButton.isEnabled = it
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.study_details_remove_alert_title))
            .setMessage(getString(R.string.study_details_remove_alert_message))
            .setNegativeButton(getString(R.string.study_details_remove)) { _, _ ->
                viewModel.deleteStudy()
                findNavController().navigateUp()
            }
            .setNeutralButton(getString(R.string.study_details_cancel), null)
            .show()
    }

    private fun showResetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.study_details_reset_alert_title))
            .setMessage(getString(R.string.study_details_reset_alert_message))
            .setPositiveButton(getString(R.string.study_details_reset)) { _, _ ->
                viewModel.resetStudy()
            }
            .setNeutralButton(getString(R.string.study_details_cancel), null)
            .show()
    }

    companion object {
        private val TAG = ActiveStudyDetailsFragment::class.java.simpleName
    }
}
