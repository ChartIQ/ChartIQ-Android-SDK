package com.chartiq.demo.ui.study

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.study.studydetails.ActiveStudyDetailsFragmentArgs
import com.chartiq.sdk.model.Study


class StudyFragment : Fragment(), ActiveStudyBottomSheetDialogFragment.DialogFragmentListener {

    private val chartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }

    private val studyViewModel: StudyViewModel by viewModels(factoryProducer = {
        StudyViewModel.ViewModelFactory(chartIQHandler)
    })
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val activeStudiesAdapter = ActiveStudiesAdapter()

    private lateinit var binding: FragmentStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.menu.findItem(R.id.add_study).setOnMenuItemClickListener {
                navigateToStudyList()
                true
            }
            activeStudiesRecyclerView.apply {
                adapter = activeStudiesAdapter
                addItemDecoration(LineItemDecoration.Default(requireContext()))
                activeStudiesAdapter.listener = object : ActiveStudiesAdapter.StudyListener {
                    override fun onOptionsClick(study: Study) {
                        ActiveStudyBottomSheetDialogFragment
                            .getInstance(study).apply {
                                setTargetFragment(this@StudyFragment, REQUEST_CODE)
                            }
                            .show(parentFragmentManager, ActiveStudyBottomSheetDialogFragment.TAG)
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(
                    SimpleItemTouchCallBack(
                        getString(R.string.study_delete).toUpperCase(),
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.coralRed))
                    ).apply {
                        onSwipeListener = object : SimpleItemTouchCallBack.OnSwipeListener {
                            override fun onSwiped(
                                viewHolder: RecyclerView.ViewHolder,
                                direction: Int,
                            ) {
                                val position = viewHolder.adapterPosition
                                val studyToDelete = activeStudiesAdapter.items[position]
                                deleteStudy(studyToDelete)
                            }
                        }
                    }
                )
                deleteItemTouchHelper.attachToRecyclerView(this)
            }

            addStudiesButton.setOnClickListener {
                navigateToStudyList()
            }
        }
        mainViewModel.activeStudies.observe(viewLifecycleOwner) { studies ->
            binding.progressBar.isVisible = false
            activeStudiesAdapter.items = studies
            binding.noActiveStudiesPlaceholder.root.isVisible = studies.isEmpty()
            binding.addStudiesButton.isVisible = studies.isEmpty()
            requireActivity().invalidateOptionsMenu()
            binding.toolbar.menu.findItem(R.id.add_study).isVisible = studies.isNotEmpty()
        }
    }

    private fun navigateToStudyList() {
        findNavController().navigate(R.id.addStudyFragment)
    }

    private fun deleteStudy(studyToDelete: Study) {
        studyViewModel.deleteStudy(studyToDelete)
        mainViewModel.fetchActiveStudyData(chartIQHandler)
    }

    override fun onDelete(study: Study) {
        studyViewModel.deleteStudy(study)
        mainViewModel.fetchActiveStudyData(chartIQHandler)

    }

    override fun onClone(study: Study) {
        studyViewModel.cloneActiveStudy(study)
        mainViewModel.fetchActiveStudyData(chartIQHandler)
    }

    override fun onSettings(study: Study) {
        val bundle = ActiveStudyDetailsFragmentArgs.Builder(study).build().toBundle()
        findNavController().navigate(R.id.activeStudyDetailsFragment, bundle)
    }

    companion object {
        private const val REQUEST_CODE = 101
    }
}
