package com.chartiq.demo.ui.add_study

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentAddStudyBinding
import com.chartiq.sdk.model.Study

//todo refactor
class AddStudyDialogFragment : DialogFragment() {

    val studiesAdapter = AllStudiesAdapter()

    val addStudiesViewModel by viewModels<AddStudyViewModel>()


    private lateinit var binding: FragmentAddStudyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.attributes?.windowAnimations = R.style.FullScreenDialog
        }
    }

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
        //todo post all studies
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
                val selectedStudies = addStudiesViewModel.selectedStudies.value ?: emptyList()
                (targetFragment as AddStudyFragmentListener).consume(selectedStudies)
                dismiss()
            }

            searchEditText.addTextChangedListener {
                addStudiesViewModel.query.postValue(it.toString())
            }

            addStudiesViewModel.filteredStudies.observe(viewLifecycleOwner) { studies ->
                studiesAdapter.items = studies

            }
            //todo add back press handler
        }
    }

    companion object {
        fun getInstance(serializedStudyList: String): AddStudyDialogFragment {
            return AddStudyDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(STUDY_LIST, serializedStudyList)
                }
            }
        }

        private const val STUDY_LIST = "STUDY_LIST"
    }

    interface AddStudyFragmentListener {
        fun consume(list: List<Study>)
    }
}
