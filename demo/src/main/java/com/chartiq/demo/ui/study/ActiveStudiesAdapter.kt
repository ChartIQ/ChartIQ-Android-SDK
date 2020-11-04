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
                val finalName = item.splitName()
                studyNameTextView.text = finalName.first
                studyValueTextView.text = finalName.second
            }
        }
    }

    interface StudyListener {
        fun onOptionsClick(study: Study)
    }

    fun Study.splitName(): Pair<String, String> {
        val result = name.split("\u200C")
        return when (result.size) {
            3 -> Pair(result[1], result[2])
            2 -> Pair(result.first(), result.last())
            else -> Pair(result.toString(), "")
        }
    }
}
