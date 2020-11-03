package com.chartiq.demo.ui.study.addstudy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemStudyBinding
import com.chartiq.sdk.model.Study

class AllStudiesAdapter : RecyclerView.Adapter<AllStudiesAdapter.StudyViewHolder>() {

    var items = listOf<Study>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var selectedItems = setOf<Study>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: StudyListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudyViewHolder(
            ItemStudyBinding.inflate(
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


    inner class StudyViewHolder(private val binding: ItemStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Study) {
            with(binding) {
                root.setOnClickListener {
                    studyCheckBox.isChecked = true
                }
                studyCheckBox.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = selectedItems.contains(item)
                    setOnCheckedChangeListener { _, isChecked ->
                        val newSet = selectedItems.toMutableSet()
                        if (isChecked) {
                            newSet.add(item)
                        } else {
                            newSet.remove(item)
                        }
                        selectedItems = newSet
                        listener?.onStudiesSelected(selectedItems.toList())
                    }
                }
                nameTextView.text = item.name
            }
        }
    }


    interface StudyListener {
        fun onStudiesSelected(studies: List<Study>)
    }
}
