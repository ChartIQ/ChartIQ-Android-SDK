package com.chartiq.demo.ui.chart.searchsymbol

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentSearchSymbolBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.searchsymbol.list.OnSearchResultClickListener
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultAdapter
import com.chartiq.demo.util.hideKeyboard
import com.google.android.material.tabs.TabLayout

import androidx.appcompat.R.id as appCompat


class SearchSymbolFragment : Fragment(), VoiceQueryReceiver {

    private lateinit var binding: FragmentSearchSymbolBinding
    private val viewModel: SearchSymbolViewModel by viewModels(factoryProducer = {
        SearchSymbolViewModel.SearchViewModelFactory(
            ChartIQNetworkManager(),
            ApplicationPrefs.Default(requireContext())
        )
    })
    private val searchAdapter = SearchResultAdapter()

    // Since the app reuses native Google voice recognition the voice query is sent to
    // main activity first and then passed to the following method
    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable?) {
            val query = s.toString()
            viewModel.updateQuery(query)
        }
    }
    private val onSearchResultClickListener = OnSearchResultClickListener { item ->
        viewModel.saveSymbol()
        requireContext().hideKeyboard(view?.windowToken)
        findNavController().navigateUp()
    }
    private val tabOnSelectListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { tab ->
                val filter = when (tab.position) {
                    TAB_ALL -> SearchFilter.ALL
                    TAB_STOCKS -> SearchFilter.STOCKS
                    TAB_FX -> SearchFilter.FX
                    TAB_INDEXES -> SearchFilter.INDEXES
                    TAB_FUNDS -> SearchFilter.FUNDS
                    TAB_FUTURES -> SearchFilter.FUTURES
                    else -> throw IllegalStateException()
                }
                viewModel.updateFilter(filter)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchSymbolBinding.inflate(inflater, container, false)

        setupViews()
        return binding.root
    }

    // Since the app reuses native Google voice recognition the voice query is sent to
    // main activity first and then passed to the following method
    override fun receiveVoiceQuery(query: String) {
        (binding.searchToolbar.menu.findItem(R.id.menu_search).actionView as SearchView)
            .setQuery(query, false)
    }

    private fun setupViews() {
        with(binding) {
            searchAdapter.listener = onSearchResultClickListener
            searchCategoryTabLayout.addOnTabSelectedListener(tabOnSelectListener)

            queryResultsRecyclerView.apply {
                this.adapter = searchAdapter
                addItemDecoration(LineItemDecoration.Default(context))
            }
            symbolNotFoundPlaceholder.applyButton.setOnClickListener {
                viewModel.saveSymbol()
                navigateBack()
            }

            viewModel.errorLiveData.observe(viewLifecycleOwner, {
                Toast.makeText(
                    requireContext(), R.string.warning_something_went_wrong, Toast.LENGTH_SHORT
                ).show()
            })
            viewModel.resultLiveData.observe(viewLifecycleOwner, { list ->
                searchAdapter.items = list

                if (list.isEmpty()) {
                    viewModel.query.value?.let { query ->
                        if (query.isNotEmpty()) {
                            symbolNotFoundPlaceholder.root.isVisible = true
                        } else {
                            binding.typeToSearchPlaceholder.root.isVisible = true
                        }
                    }
                }
            })
            viewModel.isLoading.observe(viewLifecycleOwner) { value ->
                searchSymbolProgressBar.isVisible = value

                if (value) {
                    symbolNotFoundPlaceholder.root.isVisible = false
                    typeToSearchPlaceholder.root.isVisible = false
                }
            }
        }

        with(binding.searchToolbar) {
            setNavigationOnClickListener {
                navigateBack()
            }

            (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
                val searchManager =
                    requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
                setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
                setIconifiedByDefault(false)
                isIconified = false
                maxWidth = Integer.MAX_VALUE

                findViewById<ImageView>(appCompat.search_voice_btn).setImageResource(R.drawable.ic_microphone)
                findViewById<SearchAutoComplete>(appCompat.search_src_text)
                    .addTextChangedListener(searchTextWatcher)
                findViewById<View>(appCompat.search_plate).background = null
                findViewById<View>(appCompat.submit_area).background = null
                findViewById<ImageView>(appCompat.search_mag_icon).apply {
                    isVisible = false
                    setImageDrawable(null)
                }
            }
        }
    }

    private fun navigateBack() {
        requireContext().hideKeyboard(view?.windowToken)
        findNavController().navigateUp()
    }

    companion object {
        private const val TAB_ALL = 0
        private const val TAB_STOCKS = 1
        private const val TAB_FX = 2
        private const val TAB_INDEXES = 3
        private const val TAB_FUNDS = 4
        private const val TAB_FUTURES = 5
    }
}