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
            ConditionHolderType.DIVIDER.ordinal -> DividerViewHolder(
                DividerAndBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ConditionHolderType.CONDITION_ROW.ordinal -> ConditionsViewHolder(
                ItemConditionRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ConditionHolderType.DIVIDER_SELECTOR.ordinal -> SelectDividerViewHolder(
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
            ConditionHolderType.DIVIDER_SELECTOR.ordinal
        } else {
            when (position % 2) {
                0 -> ConditionHolderType.CONDITION_ROW.ordinal
                1 -> ConditionHolderType.DIVIDER.ordinal
                else -> ConditionHolderType.DIVIDER.ordinal
            }
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(conditionItem: ConditionItem?, position: Int)
    }

    inner class ConditionsViewHolder(private val binding: ItemConditionRowBinding) :
        ViewHolder(binding.root) {

        override fun bind(conditionItem: ConditionItem?, position: Int) {
            conditionItem?.let { item ->
                val backgroundColor = Color.parseColor(conditionItem.condition.markerOption.color)
                binding.previewTextView.apply {
                    text = conditionItem.condition.markerOption.label
                    setTextColor(defineTextColor(backgroundColor))

                }
                binding.previewContainer.apply {
                    background = AppCompatResources.getDrawable(
                        binding.root.context,
                        R.drawable.item_background
                    )?.apply {
                        setTint(backgroundColor)
                    }
                    isVisible =
                        signalJoiner == SignalJoiner.OR || (signalJoiner == SignalJoiner.AND && position == 0)
                }
                binding.titleConditionTextView.text = item.title
                binding.conditionsTextView.text = item.description
                binding.root.setOnClickListener {
                    listener?.onClick(item, signalJoiner, position == 0)
                }
            }
        }

        private fun defineTextColor(backgroundColor: Int): Int {
            val red = Color.red(backgroundColor)
            val green = Color.green(backgroundColor)
            val blue = Color.blue(backgroundColor)
            return Color.parseColor(
                if (red * 0.299 + green * 0.587 + blue * 0.114 > 186) {
                    "#000000"
                } else {
                    "#ffffff"
                }
            )
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
    fun onClick(condition: ConditionItem, signalJoiner: SignalJoiner, isFirstItem: Boolean)
    fun onChangeJoiner(signalJoiner: SignalJoiner)
}
