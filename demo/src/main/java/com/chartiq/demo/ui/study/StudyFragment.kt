package com.chartiq.demo.ui.study

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
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
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentStudyBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.study.studydetails.ActiveStudyDetailsFragmentArgs
import com.chartiq.sdk.model.study.Study
import java.util.*


class StudyFragment : Fragment(), ActiveStudyBottomSheetDialogFragment.DialogFragmentListener {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val studyViewModel: StudyViewModel by viewModels(factoryProducer = {
        StudyViewModel.ViewModelFactory(chartIQ)
    })
    private val mainViewModel by activityViewModels<MainViewModel>(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    })
    private val activeStudiesAdapter = ActiveStudiesAdapter()

    private lateinit var binding: FragmentStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.fetchActiveStudyData()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.menu.findItem(R.id.add_study).setOnMenuItemClickListener {
                navigateToStudyList()
                true
            }
            toolbar.navigationIcon =
                if (BuildConfig.NEEDS_BACK_NAVIGATION) {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_back)
                } else {
                    null
                }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            activeStudiesRecyclerView.apply {
                adapter = activeStudiesAdapter
                addItemDecoration(LineItemDecoration.Default(requireContext()))
                activeStudiesAdapter.localizationManager = this@StudyFragment.localizationManager
                activeStudiesAdapter.listener = object : ActiveStudiesAdapter.StudyListener {
                    override fun onOptionsClick(study: Study) {
                        ActiveStudyBottomSheetDialogFragment
                            .getInstance(study).apply {
                                setTargetFragment(this@StudyFragment, REQUEST_CODE)
                            }
                            .show(
                                parentFragmentManager,
                                ActiveStudyBottomSheetDialogFragment::class.java.simpleName
                            )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(
                    SimpleItemTouchCallBack(
                        getString(R.string.study_delete).uppercase(Locale.getDefault()),
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.coralRed))
                    ).apply {
                        onSwipeListener = SimpleItemTouchCallBack.OnSwipeListener { viewHolder, _ ->
                            val position = viewHolder.adapterPosition
                            val studyToDelete = activeStudiesAdapter.items[position]
                            deleteStudy(studyToDelete)
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
        mainViewModel.fetchActiveStudyData()
    }

    override fun onDelete(study: Study) {
        studyViewModel.deleteStudy(study)
        mainViewModel.fetchActiveStudyData()
    }

    override fun onClone(study: Study) {
        studyViewModel.cloneActiveStudy(study)
        mainViewModel.fetchActiveStudyData()
    }

    override fun onSettings(study: Study) {
        val bundle = ActiveStudyDetailsFragmentArgs.Builder(study).build().toBundle()
        findNavController().navigate(R.id.activeStudyDetailsFragment, bundle)
    }

    companion object {
        private const val REQUEST_CODE = 101
    }
}
