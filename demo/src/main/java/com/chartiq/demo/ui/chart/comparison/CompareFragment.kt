package com.chartiq.demo.ui.chart.comparison

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentCompareBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.demo.ui.chart.panel.settings.color.ChooseColorFragment
import com.chartiq.demo.ui.chart.searchsymbol.SearchSymbolFragment
import com.chartiq.demo.ui.chart.searchsymbol.Symbol
import com.chartiq.demo.ui.common.colorpicker.toHexStringWithHash
import com.chartiq.demo.ui.study.SimpleItemTouchCallBack
import com.chartiq.sdk.model.Series

class CompareFragment : Fragment(), SearchSymbolFragment.DialogFragmentListener,
    ChooseColorFragment.DialogFragmentListener {

    private lateinit var binding: FragmentCompareBinding
    private val compareAdapter = CompareAdapter()
    private val viewModel: CompareViewModel by viewModels(
        factoryProducer = {
            CompareViewModel.ViewModelFactory((requireActivity().application as ChartIQApplication).chartIQ)
        }
    )

    private lateinit var seriesColors: List<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompareBinding.inflate(inflater, container, false)

        seriesColors = getSeriesColors()
        setupViews()
        return binding.root
    }

    override fun onChooseColor(symbolName: String, color: Int) {
        viewModel.updateSeriesParameter(symbolName, COMPARISON_PARAMETER_COLOR, color.toHexStringWithHash())
    }

    fun setupViews() {
        with(binding) {
            searchToolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                menu.findItem(R.id.add_symbol).setOnMenuItemClickListener {
                    navigateToSearchSymbol()
                    true
                }
            }
            typeToSearchPlaceholder.addSymbolButton.setOnClickListener {
                navigateToSearchSymbol()
            }

            compareRecyclerView.apply {
                compareAdapter.listener = OnSelectItemListener {
                    navigateToChooseColor(it)
                }
                adapter = compareAdapter
                val deleteItemTouchHelper = ItemTouchHelper(
                    SimpleItemTouchCallBack(
                        getString(R.string.study_delete).toUpperCase(),
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.coralRed))
                    ).apply {
                        onSwipeListener = SimpleItemTouchCallBack.OnSwipeListener { viewHolder, _ ->
                            val series = compareAdapter.items[viewHolder.adapterPosition]
                            viewModel.removeSeries(series)
                        }
                    }
                )

                deleteItemTouchHelper.attachToRecyclerView(compareRecyclerView)
            }
        }

        viewModel.series.observe(viewLifecycleOwner) { seriesList ->
            seriesList.isEmpty().also { value ->
                binding.typeToSearchPlaceholder.root.isVisible = value
                binding.searchToolbar.menu.findItem(R.id.add_symbol).isVisible = !value
            }

            compareAdapter.items = seriesList
        }
    }

    private fun navigateToChooseColor(series: Series) {
        val dialog = ChooseColorFragment.getInstance(series.symbolName, series.color)
        dialog.setTargetFragment(this, REQUEST_CODE_CHOOSE_COLOR)
        dialog.show(parentFragmentManager, null)
    }

    override fun onChooseSymbol(symbol: Symbol) {
        val color = seriesColors[compareAdapter.items.size]
        viewModel.addSeries(symbol.value, color.toHexStringWithHash())
    }

    private fun navigateToSearchSymbol() {
        val dialog = SearchSymbolFragment()
        dialog.setTargetFragment(this, REQUEST_CODE_SEARCH_SYMBOL)
        dialog.show(parentFragmentManager, null)
    }

    private fun getSeriesColors(): List<Int> {
        val colors = resources.obtainTypedArray(R.array.seriesColors)
        val colorsList = mutableListOf<Int>()
        for (index in 0 until colors.length()) {
            colorsList.add(colors.getColor(index, Color.WHITE))
        }
        colors.recycle()
        return colorsList
    }

    companion object {
        private const val REQUEST_CODE_CHOOSE_COLOR = 3000
        private const val REQUEST_CODE_SEARCH_SYMBOL = 3001
        private const val COMPARISON_PARAMETER_COLOR = "color"
    }
}
