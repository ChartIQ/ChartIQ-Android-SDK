package com.chartiq.demo.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.chartiq.demo.databinding.StudyBottomSheetBinding
import com.chartiq.sdk.model.study.Study
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ActiveStudyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: StudyBottomSheetBinding
    private val study: Study by lazy {
        requireArguments().getParcelable(ARG_STUDY)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = StudyBottomSheetBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            bottomCloneTextView.setOnClickListener {
                (targetFragment as DialogFragmentListener).onClone(study)
                dismiss()
            }
            bottomDeleteTextView.setOnClickListener {
                (targetFragment as DialogFragmentListener).onDelete(study)
                dismiss()
            }
            bottomSettingsTextView.setOnClickListener {
                (targetFragment as DialogFragmentListener).onSettings(study)
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val bottomSheet =
                    (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheet as FrameLayout).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 0
                }
            }
        })
    }

    companion object {
        fun getInstance(study: Study): ActiveStudyBottomSheetDialogFragment {
            return ActiveStudyBottomSheetDialogFragment().apply {
                arguments = bundleOf(ARG_STUDY to study)
            }
        }

        private val TAG: String = ActiveStudyBottomSheetDialogFragment::class.java.name
        private const val ARG_STUDY = "ARG_STUDY"
    }

    interface DialogFragmentListener {

        fun onDelete(study: Study)

        fun onClone(study: Study)

        fun onSettings(study: Study)
    }
}
