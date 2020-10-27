package com.chartiq.demo.ui.study

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.ChartIQCommand
import com.chartiq.demo.MainViewModel
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentStudyBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.util.Event
import com.chartiq.sdk.model.Study


class StudyFragment : Fragment() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    val activeStudiesAdapter = ActiveStudiesAdapter()

    private lateinit var binding: FragmentStudyBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
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
                        getString(R.string.delete_caps),
                        ColorDrawable(resources.getColor(R.color.coralRed))
                    ).apply {
                        onSwipeListener = object : SimpleItemTouchCallBack.OnSwipeListener {
                            override fun onSwiped(
                                viewHolder: RecyclerView.ViewHolder,
                                direction: Int
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
                // todo navigate to add studies list
            }
            mainViewModel.activeStudies.observe(viewLifecycleOwner) { studies ->
                activeStudiesAdapter.items = studies
                addStudyImageView.isVisible = studies.isNotEmpty()
                noActiveStudiesPlaceholder.root.isVisible = studies.isEmpty()
                addStudiesButton.isVisible = studies.isEmpty()
            }
        }
    }

    private fun refreshActiveStudies() {
        mainViewModel.chartEvent.postValue(Event(ChartIQCommand.GetActiveStudies))
    }

    fun deleteStudy(studyToDelete: Study) {
        mainViewModel.chartEvent.postValue(
            Event(
                ChartIQCommand.DeleteStudy(
                    studyToDelete
                )
            )
        )
        refreshActiveStudies()
    }
}
