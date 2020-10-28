package com.chartiq.demo.ui.add_study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.databinding.FragmentAddStudyBinding
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.sdk.model.Study

class AddStudyFragment : Fragment() {

    private val studiesAdapter = AllStudiesAdapter()

    private val chartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }

    val addStudiesViewModel by viewModels<AddStudyViewModel>(factoryProducer = {
        AddStudyViewModel.ViewModelFactory(chartIQHandler)
    })
    val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentAddStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddStudyBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            studiesRecyclerView.apply {
                adapter = studiesAdapter
                studiesAdapter.listener = object : AllStudiesAdapter.StudyListener {
                    override fun onStudiesSelected(studies: List<Study>) {
                        addStudiesViewModel.selectedStudies.postValue(studies)
                    }
                }
            }
            addStudyTextView.setOnClickListener {
                addStudiesViewModel.saveStudies()
//                mainViewModel.fetchActiveStudyData(chartIQHandler)
                findNavController().navigateUp()
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            searchEditText.addTextChangedListener {
                addStudiesViewModel.query.postValue(it.toString())
            }

            addStudiesViewModel.filteredStudies.observe(viewLifecycleOwner) { studies ->
                studiesAdapter.items = studies
            }
        }
    }
}
