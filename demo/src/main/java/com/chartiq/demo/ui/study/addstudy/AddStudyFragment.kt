package com.chartiq.demo.ui.study.addstudy

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentAddStudyBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.sdk.model.study.Study

class AddStudyFragment : Fragment() {

    private val addStudiesViewModel by viewModels<AddStudyViewModel>(factoryProducer = {
        AddStudyViewModel.ViewModelFactory(chartIQHandler)
    })
    private val mainViewModel by activityViewModels<MainViewModel>(factoryProducer = {
        MainViewModel.ViewModelFactory(
                ChartIQNetworkManager(),
                ApplicationPrefs.Default(requireContext()),
                chartIQHandler)
    })
    private val studiesAdapter = AllStudiesAdapter()

    private val chartIQHandler by lazy {
        (requireActivity().application as ChartIQApplication).chartIQHandler
    }

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
            progressBar.isVisible = true
            studiesRecyclerView.apply {
                adapter = studiesAdapter
                studiesAdapter.listener = object : AllStudiesAdapter.StudyListener {
                    override fun onStudiesSelected(studies: List<Study>) {
                        addStudiesViewModel.onStudiesSelect(studies)
                    }
                }
            }
            toolbar.menu.findItem(R.id.action_done).setOnMenuItemClickListener {
                progressBar.isVisible = true
                addStudiesViewModel.saveStudies()
                mainViewModel.fetchActiveStudyData()
                hideKeyboard()
                findNavController().navigateUp()
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            searchEditText.addTextChangedListener {
                progressBar.isVisible = true
                addStudiesViewModel.onNewQuery(it.toString())
            }
        }
        addStudiesViewModel.filteredStudies.observe(viewLifecycleOwner) { studies ->
            binding.progressBar.isVisible = false
            studiesAdapter.items = studies
        }

    }

    private fun hideKeyboard() {
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
