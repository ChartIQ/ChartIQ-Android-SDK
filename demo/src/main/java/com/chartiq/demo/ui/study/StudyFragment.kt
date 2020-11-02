package com.chartiq.demo.ui.study

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.chartiq.sdk.model.Study


class StudyFragment : Fragment() {

    private val chartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }

    private val studyViewModel: StudyViewModel by viewModels(factoryProducer = {
        StudyViewModel.ViewModelFactory(chartIQHandler)
    })
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val activeStudiesAdapter = ActiveStudiesAdapter()

    private lateinit var binding: FragmentStudyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

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
                // todo navigate to add studies list
                true
            }
            activeStudiesRecyclerView.apply {
                adapter = activeStudiesAdapter
                addItemDecoration(LineItemDecoration.Default(requireContext()))
                activeStudiesAdapter.listener = object : ActiveStudiesAdapter.StudyListener {
                    override fun onOptionsClick(study: Study) {
                        // todo open bottom sheet
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(
                    SimpleItemTouchCallBack(
                        getString(R.string.delete).toUpperCase(),
                        ColorDrawable(resources.getColor(R.color.coralRed))
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
                findNavController().navigate(R.id.addStudyFragment)
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

    fun deleteStudy(studyToDelete: Study) {
        studyViewModel.deleteStudy(studyToDelete)
        mainViewModel.fetchActiveStudyData(chartIQHandler)
    }
}
