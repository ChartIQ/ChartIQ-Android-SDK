package com.chartiq.demo.ui.signal

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.chartiq.demo.BuildConfig
import com.chartiq.demo.ChartIQApplication
import com.chartiq.demo.R
import com.chartiq.demo.ServiceLocator
import com.chartiq.demo.databinding.FragmentSignalBinding
import com.chartiq.demo.network.ChartIQNetworkManager
import com.chartiq.demo.ui.LineItemDecoration
import com.chartiq.demo.ui.MainViewModel
import com.chartiq.demo.ui.study.SimpleItemTouchCallBack
import com.chartiq.sdk.model.signal.Signal
import java.util.*


class SignalFragment : Fragment() {

    private val chartIQ by lazy {
        (requireActivity().application as ChartIQApplication).chartIQ
    }
    private val localizationManager by lazy {
        (requireActivity().application as ChartIQApplication).localizationManager
    }
    private val signalViewModel: SignalViewModel by viewModels(factoryProducer = {
        SignalViewModel.ViewModelFactory(chartIQ)
    })
    private val mainViewModel by activityViewModels<MainViewModel>(factoryProducer = {
        MainViewModel.ViewModelFactory(
            ChartIQNetworkManager(),
            (requireActivity().application as ServiceLocator).applicationPreferences,
            chartIQ,
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )
    })
    private val signalsAdapter = SignalsAdapter()

    private lateinit var binding: FragmentSignalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignalBinding.inflate(inflater, container, false)
        setupViews()
        mainViewModel.fetchActiveSignalData()
        return binding.root
    }

    private fun setupViews() {
        with(binding) {
            toolbar.menu.findItem(R.id.add_signal).setOnMenuItemClickListener {
                navigateToAddSignal()
                true
            }
            toolbar.navigationIcon =
                if (BuildConfig.NEEDS_BACK_NAVIGATION) {
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_nav_back)
                } else {
                    null
                }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            activeSignalsRecyclerView.apply {
                adapter = signalsAdapter
                addItemDecoration(LineItemDecoration.Default(requireContext()))
                signalsAdapter.localizationManager = this@SignalFragment.localizationManager
                signalsAdapter.listener = object : SignalsAdapter.SignalListener {
                    override fun onSignalClick(signal: Signal) {
                        findNavController().navigate(
                            SignalFragmentDirections.actionSignalFragmentToAddSignalFragment(
                                signal
                            )
                        )
                    }

                    override fun onSignalToggled(signal: Signal, isChecked: Boolean) {
                        signalViewModel.toggledSignal(signal, isChecked)
                        mainViewModel.fetchActiveSignalData()
                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(
                    SimpleItemTouchCallBack(
                        getString(R.string.study_delete).uppercase(Locale.getDefault()),
                        ColorDrawable(ContextCompat.getColor(requireContext(), R.color.coralRed))
                    ).apply {
                        onSwipeListener = SimpleItemTouchCallBack.OnSwipeListener { viewHolder, _ ->
                            val position = viewHolder.adapterPosition
                            val signalToDelete = signalsAdapter.items[position]
                            signalViewModel.deleteSignal(signalToDelete)
                            mainViewModel.fetchActiveSignalData()
                        }
                    }
                )
                deleteItemTouchHelper.attachToRecyclerView(this)
            }

            addSignalButton.setOnClickListener {
                navigateToAddSignal()
            }
        }
        mainViewModel.activeSignals.observe(viewLifecycleOwner) { signals ->
            binding.progressBar.isVisible = false
            signalsAdapter.items = signals
            binding.noActiveSignalsPlaceholder.root.isVisible = signals.isEmpty()
            binding.addSignalButton.isVisible = signals.isEmpty()
            requireActivity().invalidateOptionsMenu()
            binding.toolbar.menu.findItem(R.id.add_signal).isVisible = signals.isNotEmpty()
        }
    }

    private fun navigateToAddSignal() {
        findNavController().navigate(
            SignalFragmentDirections.actionSignalFragmentToAddSignalFragment(
                null
            )
        )
    }

}
