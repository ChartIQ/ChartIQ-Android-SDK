package com.chartiq.demo.ui.chart.panel.settings

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chartiq.demo.R
import com.chartiq.demo.databinding.ItemDrawingToolSettingChooseValueBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingColorBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingDeviationBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingLineTypeBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingNumberBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingStyleBinding
import com.chartiq.demo.databinding.ItemDrawingToolSettingSwitchBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.getLineTypeResource
import com.chartiq.demo.ui.common.colorpicker.COLOR_AUTO


class DrawingToolSettingsAdapter :
    RecyclerView.Adapter<DrawingToolSettingsAdapter.SettingViewHolder>() {

    var items = listOf<DrawingToolSettingsItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: OnSelectItemListener<DrawingToolSettingsItem>? = null

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DrawingToolSettingsItem.ChooseValue -> VIEW_TYPE_CHOOSE_VALUE
            is DrawingToolSettingsItem.Color -> VIEW_TYPE_COLOR
            is DrawingToolSettingsItem.Line -> VIEW_TYPE_LINE
            is DrawingToolSettingsItem.Style -> VIEW_TYPE_STYLE
            is DrawingToolSettingsItem.Switch -> VIEW_TYPE_SWITCH
            is DrawingToolSettingsItem.Deviation -> VIEW_TYPE_DEVIATION
            is DrawingToolSettingsItem.Number -> VIEW_TYPE_NUMBER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CHOOSE_VALUE -> ChooseValueViewHolder(
                ItemDrawingToolSettingChooseValueBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_LINE -> LineViewHolder(
                ItemDrawingToolSettingLineTypeBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_SWITCH -> SwitchViewHolder(
                ItemDrawingToolSettingSwitchBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_COLOR -> ColorViewHolder(
                ItemDrawingToolSettingColorBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_STYLE -> StyleViewHolder(
                ItemDrawingToolSettingStyleBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_DEVIATION -> DeviationViewHolder(
                ItemDrawingToolSettingDeviationBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_NUMBER -> NumberViewHolder(
                ItemDrawingToolSettingNumberBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    abstract class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: DrawingToolSettingsItem)
    }

    inner class ChooseValueViewHolder(private val binding: ItemDrawingToolSettingChooseValueBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.ChooseValue
            with(binding) {
                titleTextView.text = root.resources.getString(item.title)
                val secondaryTextVisible = item.secondaryText.isNotEmpty()
                if (secondaryTextVisible) {
                    secondaryTextView.text = item.secondaryText
                }
                secondaryTextView.isVisible = secondaryTextVisible
                root.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }

    inner class ColorViewHolder(private val binding: ItemDrawingToolSettingColorBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.Color
            with(binding) {
                val color = if (item.color.isEmpty() || item.color == COLOR_AUTO) {
                    ResourcesCompat.getColor(
                        binding.root.resources,
                        R.color.studyParameterAutoColor,
                        null
                    )
                } else {
                    Color.parseColor(item.color)
                }
                titleTextView.text = root.resources.getString(item.title)
                (colorImageView.background as GradientDrawable).setColor(color)
                root.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }

    inner class LineViewHolder(private val binding: ItemDrawingToolSettingLineTypeBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.Line
            with(binding) {
                titleTextView.text = root.resources.getString(item.title)
                val lineType = item.lineType.getLineTypeResource(item.lineWidth)
                lineImageView.setImageResource(lineType)
                root.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }

    inner class StyleViewHolder(private val binding: ItemDrawingToolSettingStyleBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.Style
            with(binding) {
                titleTextView.text = root.resources.getString(item.title)
                val boldStrokeColor = if (item.isBold) {
                    R.color.mountainMeadow
                } else {
                    R.color.brightGrey
                }
                val italicStokeColor = if (item.isItalic) {
                    R.color.mountainMeadow
                } else {
                    R.color.brightGrey
                }

                boldImageView.apply {
                    DrawableCompat.setTint(
                        foreground.mutate(),
                        ContextCompat.getColor(root.context, boldStrokeColor)
                    )
                    setOnClickListener {
                        listener?.onSelected(item.copy(isBold = !item.isBold))
                    }
                }

                italicImageView.apply {
                    DrawableCompat.setTint(
                        foreground.mutate(),
                        ContextCompat.getColor(root.context, italicStokeColor)
                    )
                    setOnClickListener {
                        listener?.onSelected(item.copy(isItalic = !item.isItalic))
                    }
                }
            }
        }
    }

    inner class SwitchViewHolder(private val binding: ItemDrawingToolSettingSwitchBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.Switch
            with(binding) {
                titleTextView.text = binding.root.resources.getString(item.title)
                itemSwitch.isChecked = item.checked
                itemSwitch.setOnCheckedChangeListener { button, isChecked ->
                    if (button.isPressed) {
                        listener?.onSelected(item.copy(checked = isChecked))
                    }
                }
            }
        }
    }

    inner class DeviationViewHolder(private val binding: ItemDrawingToolSettingDeviationBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            item as DrawingToolSettingsItem.Deviation
            with(binding) {
                titleTextView.text = root.resources.getString(item.title)
                root.setOnClickListener {
                    listener?.onSelected(item)
                }
            }
        }
    }

    inner class NumberViewHolder(private val binding: ItemDrawingToolSettingNumberBinding) :
        SettingViewHolder(binding.root) {

        override fun bind(item: DrawingToolSettingsItem) {
            val numberItem = item as DrawingToolSettingsItem.Number
            with(binding) {
                priceBucketsEditText.requestFocus()
                priceBucketsTextInputLayout.hint = root.resources.getString(numberItem.title)
                priceBucketsEditText.setText(numberItem.number.toString())
                priceBucketsEditText.text?.length?.let { priceBucketsEditText.setSelection(it) }
                priceBucketsEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(text: Editable?) {
                        if (text.toString() != item.number.toString() && text?.isNotEmpty() == true) {
                            listener?.onSelected(numberItem.copy(number = text.toString().toInt()))
                        }
                        priceBucketsEditText.removeTextChangedListener(this)
                    }
                })
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_CHOOSE_VALUE = 0
        private const val VIEW_TYPE_LINE = 1
        private const val VIEW_TYPE_SWITCH = 2
        private const val VIEW_TYPE_COLOR = 3
        private const val VIEW_TYPE_STYLE = 4
        private const val VIEW_TYPE_DEVIATION = 5
        private const val VIEW_TYPE_NUMBER = 6
    }
}
