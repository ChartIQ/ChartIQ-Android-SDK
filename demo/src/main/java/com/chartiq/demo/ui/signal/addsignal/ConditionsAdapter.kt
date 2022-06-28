package com.chartiq.demo.ui.signal.addsignal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.DividerAndBinding
import com.chartiq.demo.databinding.DividerSelectBinding
import com.chartiq.demo.databinding.ItemConditionRowBinding
import com.chartiq.sdk.model.signal.Condition
import com.chartiq.sdk.model.signal.SignalJoiner

class ConditionsAdapter : RecyclerView.Adapter<ConditionsAdapter.ViewHolder>() {

    var listener: ConditionsClickListener? = null

    var items: List<ConditionItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var signalJoiner = SignalJoiner.OR

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> DividerViewHolder(
                DividerAndBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            1 -> ConditionsViewHolder(
                ItemConditionRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            2 -> SelectDividerViewHolder(
                DividerSelectBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> DividerViewHolder(
                DividerAndBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position / 2], position)
    }

    override fun getItemCount(): Int {
        return if (items.size == 1) {
            items.size
        } else {
            items.size * 2 - 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 1) {
            2
        } else {
            (position + 1) % 2
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(conditionItem: ConditionItem?, position: Int)
    }

    inner class ConditionsViewHolder(private val binding: ItemConditionRowBinding) :
        ViewHolder(binding.root) {

        override fun bind(conditionItem: ConditionItem?, position: Int) {
            conditionItem?.let { item ->
                binding.previewTextView.apply {
                    isVisible =
                        signalJoiner == SignalJoiner.OR || (signalJoiner == SignalJoiner.AND && position == 0)
                    background = AppCompatResources.getDrawable(
                        context,
                        R.drawable.item_background
                    )?.apply {
                        setTint(Color.parseColor(conditionItem.condition.color))
                    }
                }
                binding.titleConditionTextView.text = item.title
                binding.conditionsTextView.text = item.description
                binding.root.setOnClickListener {
                    listener?.onClick(item.condition)
                }
            }
        }
    }

    inner class DividerViewHolder(private val binding: DividerAndBinding) :
        ViewHolder(binding.root) {

        override fun bind(conditionItem: ConditionItem?, position: Int) {
            binding.and.text = when (signalJoiner) {
                SignalJoiner.AND -> binding.root.context.getString(R.string.signal_and)
                SignalJoiner.OR -> binding.root.context.getString(R.string.signal_or)
            }
        }
    }

    inner class SelectDividerViewHolder(private val binding: DividerSelectBinding) :
        ViewHolder(binding.root) {

        override fun bind(conditionItem: ConditionItem?, position: Int) {
            binding.andButton.isSelected = signalJoiner == SignalJoiner.AND
            binding.orButton.isSelected = signalJoiner == SignalJoiner.OR
            binding.andButton.setOnClickListener {
                signalJoiner = SignalJoiner.AND
                listener?.onChangeJoiner(SignalJoiner.AND)
                notifyDataSetChanged()
            }
            binding.orButton.setOnClickListener {
                signalJoiner = SignalJoiner.OR
                listener?.onChangeJoiner(SignalJoiner.OR)
                notifyDataSetChanged()
            }
        }
    }
}

interface ConditionsClickListener {
    fun onClick(condition: Condition)
    fun onChangeJoiner(signalJoiner: SignalJoiner)
}
