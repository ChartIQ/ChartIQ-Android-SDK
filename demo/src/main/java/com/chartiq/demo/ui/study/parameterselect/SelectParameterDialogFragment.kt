package com.chartiq.demo.ui.study.parameterselect

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentSelectParameterBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.sdk.model.StudyParameter

class SelectParameterDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentSelectParameterBinding

    private val selectParameter: StudyParameter.Select by lazy {
        SelectParameterDialogFragmentArgs.fromBundle(requireArguments()).parameter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectParameterBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.attributes?.windowAnimations = R.style.FullScreenDialog
        }
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            val optionsAdapter = SelectOptionsAdapter().apply {
                items = selectParameter.options
                selectedValue = selectParameter.value
                listener = object : SelectOptionsAdapter.SelectOptionsAdapterListener {
                    override fun onSelect(keyValue: Map.Entry<String, String>) {
                        (targetFragment as DialogFragmentListener).onSelectOption(
                            selectParameter,
                            keyValue.key,
                        )
                        dismiss()
                    }
                }
            }
            parametersRecyclerView.apply {
                adapter = optionsAdapter
                addItemDecoration(LineItemDecoration.Default(requireContext()))
            }
        }
    }

    companion object {
        fun getInstance(bundle: Bundle): SelectParameterDialogFragment {
            return SelectParameterDialogFragment().apply {
                arguments = bundle
            }
        }
    }

    interface DialogFragmentListener {
        fun onSelectOption(parameter: StudyParameter.Select, newValue: String)
    }
}
