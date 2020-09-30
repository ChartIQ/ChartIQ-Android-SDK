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
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chartiq.demo.ApplicationPrefs
import com.chartiq.demo.R
import com.chartiq.demo.databinding.FragmentSearchSymbolBinding
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.chart.searchsymbol.list.OnSearchResultClickListener
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultAdapter
import com.chartiq.demo.ui.chart.searchsymbol.list.SearchResultItem


class SearchSymbolFragment : Fragment(), TextWatcher, OnSearchResultClickListener {

    private lateinit var binding: FragmentSearchSymbolBinding
    private val viewModel: SearchSymbolViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchSymbolBinding.inflate(inflater, container, false)

        setupUI()
        return binding.root
    }

    // Since the app reuses native Google voice recognition the voice query is sent to
    // main activity first and then passed to the following method
    fun receiveVoiceQuery(query: String) {
        (binding.searchToolbar.menu.findItem(R.id.menu_search).actionView as SearchView)
            .setQuery(query, false)
    }

    private fun setupUI() {
        binding.searchToolbar.apply {
            setNavigationOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }

            val searchManager =
                requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
                findViewById<ImageView>(androidx.appcompat.R.id.search_voice_btn).apply {
                    setImageResource(R.drawable.ic_microphone)
                }
                findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text).apply {
                    addTextChangedListener(this@SearchSymbolFragment)
                }
                findViewById<View>(androidx.appcompat.R.id.search_plate).apply {
                    background = null
                }
                findViewById<View>(androidx.appcompat.R.id.submit_area).apply {
                    background = null
                }
                findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon).apply {
                    visibility = View.GONE
                    setImageDrawable(null)
                }
                isIconified = false
                setIconifiedByDefault(false)
                setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            }
        }
        val searchAdapter = SearchResultAdapter(this)
        binding.queryResultsRecyclerView.apply {
            this.adapter = searchAdapter
            addItemDecoration(LineItemDecoration.Default(context))
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner, { list ->
            searchAdapter.setList(list)
            binding.searchSymbolProgressBar.visibility = View.GONE
            binding.queryResultsRecyclerView.visibility = View.VISIBLE
        })
    }

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

    private fun hideKeyboard() {
        (context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onSearchItemClick(item: SearchResultItem) {
        ApplicationPrefs
            .Default(requireContext())
            .saveChartSymbol(Symbol(item.symbol))
        hideKeyboard()
        findNavController().navigateUp()
    }
}
