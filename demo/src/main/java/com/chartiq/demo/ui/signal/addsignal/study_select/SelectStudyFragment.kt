package com.chartiq.demo.ui.signal.addsignal.study_select

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
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentSelectStudyBinding
import com.chartiq.demo.ui.OnBackPressed
import com.chartiq.demo.ui.signal.addsignal.AddSignalViewModel
import com.chartiq.sdk.model.study.Study

class SelectStudyFragment : Fragment(), OnBackPressed {
    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val selectStudiesViewModel by activityViewModels<AddSignalViewModel>(factoryProducer = {
        AddSignalViewModel.ViewModelFactory(chartIQ, localizationManager, requireContext())
    })

    private val studiesAdapter = AllStudiesAdapter()

    private lateinit var binding: FragmentSelectStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectStudyBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            progressBar.isVisible = true
            studiesRecyclerView.apply {
                adapter = studiesAdapter
                studiesAdapter.localizationManager = this@SelectStudyFragment.localizationManager
                studiesAdapter.listener = object : AllStudiesAdapter.StudyListener {
                    override fun onStudiesSelected(study: Study) {
                        selectStudiesViewModel.onStudySelect(study)
                    }

                }
            }
            val toolbarMenu = toolbar.menu.findItem(R.id.action_done)
            selectStudiesViewModel.isSaveStudyAvailable.observe(viewLifecycleOwner) { isActive ->
                toolbarMenu.isEnabled = isActive
            }
            toolbarMenu.setOnMenuItemClickListener {
                selectStudiesViewModel.onStudyApproved()
                progressBar.isVisible = true
                hideKeyboard()
                findNavController().navigateUp()
            }

            toolbar.setNavigationOnClickListener {
                selectStudiesViewModel.onClearStudy()
                hideKeyboard()
                findNavController().navigateUp()
            }

            searchEditText.addTextChangedListener {
                progressBar.isVisible = true
                selectStudiesViewModel.onNewQuery(it.toString())
            }
        }
        selectStudiesViewModel.filteredStudies.observe(viewLifecycleOwner) { studies ->
            binding.progressBar.isVisible = false
            studiesAdapter.apply {
                items = studies
                selectedItem = selectStudiesViewModel.selectedStudy.value
            }
        }

    }

    private fun hideKeyboard() {
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onBackPressed() {
        selectStudiesViewModel.onClearStudy()
    }
}
