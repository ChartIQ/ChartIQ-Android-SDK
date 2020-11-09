package com.chartiq.demo.ui.study.studydetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyDetailsBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.sdk.model.Study

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
            toolbar.title = study.type
            toolbar.menu.findItem(R.id.action_clone_details).setOnMenuItemClickListener {
                viewModel.cloneStudy()
                true
            }
            toolbar.menu.findItem(R.id.action_reset_details).setOnMenuItemClickListener {
                showResetDialog()
                true
            }
            toolbar.menu.findItem(R.id.action_delete_details).setOnMenuItemClickListener {
                showDeleteDialog()
                true
            }
            detailsRecyclerView.adapter = studyDetailsAdapter
        }
        viewModel.params.observe(viewLifecycleOwner){

        }
        viewModel.studyParams.observe(viewLifecycleOwner) {
            studyDetailsAdapter.items = it
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Do You Want To Remove This Study?")
            .setMessage("This study will be removed from the current chart.")
            .setNegativeButton("Remove", null)
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun showResetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Do You Want To Reset This Study To Defaults?")
            .setMessage("This study will be reset to default options.")
            .setPositiveButton("Reset", null)
            .setNeutralButton("Cancel", null)
            .show()
    }
}