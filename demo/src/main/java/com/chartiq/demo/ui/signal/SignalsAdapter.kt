package com.chartiq.demo.ui.signal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.databinding.ItemSignalBinding
import com.chartiq.demo.localization.LocalizationManager
import com.chartiq.sdk.model.signal.Signal

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
                signalNameTextView.text = item.name
                signalToggle.isChecked = !item.disabled

                signalToggle.setOnClickListener {
                    listener?.onSignalToggled(item, !item.disabled)
                }
            }
        }
    }

    interface SignalListener {
        fun onSignalClick(signal: Signal)
        fun onSignalToggled(signal: Signal, isChecked: Boolean)
    }
}
