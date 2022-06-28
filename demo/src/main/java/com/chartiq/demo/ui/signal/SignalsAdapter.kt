package com.chartiq.demo.ui.signal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSignalBinding
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.sdk.model.signal.Signal
import com.chartiq.sdk.model.study.Study

class SignalsAdapter : RecyclerView.Adapter<SignalsAdapter.StudyViewHolder>() {

    var localizationManager: LocalizationManager? = null

    var items = listOf<Signal>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: SignalListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudyViewHolder(
            ItemSignalBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StudyViewHolder(private val binding: ItemSignalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Signal) {
            with(binding) {
                root.setOnClickListener {
                    listener?.onSignalClick(item)
                }
                signalNameTextView.text =
                    localizationManager?.getTranslationFromValue(item.name, root.context) ?: ""

                signalCheckBox.isChecked = item.reveal
            }
        }
    }

    fun Study.splitName(): Pair<String, String> {
        val nameWithoutLeading = name.replaceFirst(ZERO_WIDTH_NON_JOINER.toString(), "")
        return if (nameWithoutLeading.split(ZERO_WIDTH_NON_JOINER).size == 1) {
            Pair(nameWithoutLeading, "")
        } else {
            val indexOfDelimiter = nameWithoutLeading.indexOfFirst { it == ZERO_WIDTH_NON_JOINER }
            Pair(
                nameWithoutLeading
                    .substring(0, indexOfDelimiter)
                    .trim(),
                nameWithoutLeading
                    .substring(indexOfDelimiter)
                    .replace(ZERO_WIDTH_NON_JOINER.toString(), "")
                    .trim()
            )
        }
    }

    companion object {
        private const val ZERO_WIDTH_NON_JOINER = '\u200C'
    }

    interface SignalListener {
        fun onSignalClick(signal: Signal)
    }
}
