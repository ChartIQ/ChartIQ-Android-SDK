package com.chartiq.demo.ui.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemStudyActiveBinding
import com.chartiq.sdk.model.study.Study

class ActiveStudiesAdapter : RecyclerView.Adapter<ActiveStudiesAdapter.StudyViewHolder>() {

    var items = listOf<Study>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: StudyListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudyViewHolder(
            ItemStudyActiveBinding.inflate(
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

    inner class StudyViewHolder(private val binding: ItemStudyActiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Study) {
            with(binding) {
                optionsImageView.setOnClickListener {
                    listener?.onOptionsClick(item)
                }
                studyNameTextView.setOnClickListener {
                    listener?.onOptionsClick(item)
                }
                studyValueTextView.setOnClickListener {
                    listener?.onOptionsClick(item)
                }
                val finalName = item.splitName()
                studyNameTextView.text = finalName.first
                studyValueTextView.text = finalName.second
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

    interface StudyListener {
        fun onOptionsClick(study: Study)
    }
}
