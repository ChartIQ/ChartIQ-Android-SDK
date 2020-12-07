package com.chartiq.demo.ui.study.studydetails

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyDetailsBinding
import com.chartiq.demo.ui.study.parameterselect.SelectParameterDialogFragment
import com.chartiq.sdk.model.study.Study
import com.chartiq.sdk.model.study.StudyParameter
import kotlinx.android.synthetic.main.fragment_study_details.*

class ActiveStudyDetailsFragment : Fragment(), SelectParameterDialogFragment.DialogFragmentListener {

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
    ): View {
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
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            detailsRecyclerView.apply {
                adapter = studyDetailsAdapter.apply {
                    listener = object : StudyDetailsAdapter.StudyParameterListener {
                        override fun onCheckboxParamChange(parameter: StudyParameter.Checkbox, isChecked: Boolean) {
                            viewModel.onCheckboxParamChange(parameter, isChecked)
                        }

                        override fun onTextParamChange(parameter: StudyParameter, newValue: String) {
                            viewModel.onTextParamChange(parameter, newValue)
                            Log.i(TAG, "onTextParamChange $newValue")

                        }

                        override fun onNumberParamChange(parameter: StudyParameter.Number, newValue: Double?) {
                            viewModel.onNumberParamChange(parameter, newValue)
                            Log.i(TAG, "onNumberParamChange $newValue")

                        }

                        override fun onColorParamChange(studyParameter: StudyParameter) {
                            //todo open color picker
                            Log.i(TAG, "onColorParamChange")
                        }

                        override fun onSelectParamChange(studyParameter: StudyParameter.Select) {
                            SelectParameterDialogFragment.getInstance(studyParameter).apply {
                                setTargetFragment(this@ActiveStudyDetailsFragment, REQUEST_CODE)
                            }.show(parentFragmentManager, SelectParameterDialogFragment::class.java.simpleName)
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
        private const val REQUEST_CODE = 102

    }

    override fun onSelect(parameter: StudyParameter.Select, newValue: String) {
        viewModel.onSelectChange(parameter, newValue)
    }
}
