package com.chartiq.demo.ui.study

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemActiveStudyBinding
import com.chartiq.sdk.model.Study

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
            ItemActiveStudyBinding.inflate(
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

    inner class StudyViewHolder(private val binding: ItemActiveStudyBinding) :
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
                val finalName = parseName(item.name)
                studyNameTextView.text = finalName.first
                studyValueTextView.text = finalName.second
            }
        }

        private fun parseName(name: String): Pair<String, String> {
            // Divide a name by 'ZERO WIDTH NON-JOINER' (U+200C)
            val result = name.split(ZERO_WIDTH_NON_JOINER)
            return when (result.size) {
                3 -> Pair(result[1], result[2])
                2 -> Pair(result.first(), result.last())
                else -> Pair(result.toString(), "")
            }
        }
    }

    interface StudyListener {
        fun onOptionsClick(study: Study)
    }

    companion object {
        private const val ZERO_WIDTH_NON_JOINER = "\u200C"
    }
}
