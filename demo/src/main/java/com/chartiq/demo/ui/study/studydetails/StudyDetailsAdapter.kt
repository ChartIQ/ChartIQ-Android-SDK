package com.chartiq.demo.ui.study.studydetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemActiveStudyBinding

class StudyDetailsAdapter : RecyclerView.Adapter<StudyDetailsAdapter.StudyViewHolder>() {
    var items = listOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudyViewHolder {
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

    class StudyViewHolder(private val binding: ItemActiveStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Any) {
            with(binding) {

            }
        }
    }
}