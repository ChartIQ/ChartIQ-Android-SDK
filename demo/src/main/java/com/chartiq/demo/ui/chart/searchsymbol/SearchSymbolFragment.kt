package com.chartiq.demo.ui.chart.searchsymbol

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
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
import androidx.appcompat.R.id as appCompat


class SearchSymbolFragment : Fragment() {

    private lateinit var binding: FragmentSearchSymbolBinding
    private val viewModel: SearchSymbolViewModel by viewModels(factoryProducer = {
        SearchSymbolViewModel.SearchViewModelFactory(ChartIQNetworkManager())
    })

    // Since the app reuses native Google voice recognition the voice query is sent to
    // main activity first and then passed to the following method
    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.run {
                typeToSearchPlaceHolder.root.visibility = View.GONE
                queryResultsRecyclerView.visibility = View.INVISIBLE
                searchSymbolProgressBar.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(s: Editable?) {
            val query = s.toString()
            if (query.isNotEmpty()) {
                viewModel.fetchSymbol(query)
            } else {
                binding.typeToSearchPlaceHolder.root.visibility = View.VISIBLE
            }
        }
    }
    private val onSearchResultClickListener = OnSearchResultClickListener { item ->
        ApplicationPrefs
            .Default(requireContext())
            .saveChartSymbol(Symbol(item.symbol))
        hideKeyboard()
        findNavController().navigateUp()
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

    private fun setupViews() {
        with(binding) {
            val searchAdapter = SearchResultAdapter(onSearchResultClickListener)
            queryResultsRecyclerView.apply {
                this.adapter = searchAdapter
                addItemDecoration(LineItemDecoration.Default(context))
            }
            viewModel.errorLiveData.observe(viewLifecycleOwner, {
                searchSymbolProgressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(), R.string.warning_something_went_wrong, Toast.LENGTH_SHORT
                ).show()
            })
            viewModel.resultLiveData.observe(viewLifecycleOwner, { list ->
                searchAdapter.list = list
                searchSymbolProgressBar.visibility = View.GONE
                queryResultsRecyclerView.visibility = View.VISIBLE
            })
        }

        with(binding.searchToolbar) {
            setNavigationOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }

            (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
                val searchManager =
                    requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
                setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
                setIconifiedByDefault(false)
                isIconified = false

                findViewById<ImageView>(appCompat.search_voice_btn).setImageResource(R.drawable.ic_microphone)
                findViewById<SearchAutoComplete>(appCompat.search_src_text)
                    .addTextChangedListener(searchTextWatcher)
                findViewById<View>(appCompat.search_plate).background = null
                findViewById<View>(appCompat.submit_area).background = null
                findViewById<ImageView>(appCompat.search_mag_icon).apply {
                    visibility = View.GONE
                    setImageDrawable(null)
                }
            }
        }
    }

    private fun hideKeyboard() {
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
