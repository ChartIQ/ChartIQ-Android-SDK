package com.chartiq.demo.ui.signal.addsignal.study_select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSelectStudyBinding
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.sdk.model.study.Study

class AllStudiesAdapter(

) : RecyclerView.Adapter<AllStudiesAdapter.StudyViewHolder>() {

    var localizationManager: LocalizationManager? = null

    var items = listOf<Study>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var selectedItem: Study? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: StudyListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudyViewHolder(
            ItemSelectStudyBinding.inflate(
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


    inner class StudyViewHolder(private val binding: ItemSelectStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Study) {
            with(binding) {
                checkImageView.isVisible = selectedItem == item
                root.setOnClickListener {
                    selectedItem = item
                    checkImageView.isVisible = true
                    listener?.onStudiesSelected(item)
                }

                studyTextView.text =
                    localizationManager?.getTranslationFromValue(item.name, root.context)
                        ?: item.name
            }
        }
    }

    interface StudyListener {
        fun onStudiesSelected(study: Study)
    }
}
