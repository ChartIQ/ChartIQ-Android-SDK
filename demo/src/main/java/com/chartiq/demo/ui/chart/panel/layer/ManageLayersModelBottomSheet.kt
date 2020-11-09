package com.chartiq.demo.ui.chart.panel.layer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chartiq.demo.R
import com.chartiq.demo.databinding.DialogManageLayersBinding
import com.chartiq.demo.ui.chart.panel.OnSelectItemListener
import com.chartiq.sdk.model.ChartLayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManageLayersModelBottomSheet : BottomSheetDialogFragment() {

    private val onLayerSelectedListener = OnSelectItemListener<LayerItem> {
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogManageLayersBinding.inflate(inflater, container, false)

        setupViews(binding)
        return binding.root
    }

    private fun setupViews(binding: DialogManageLayersBinding) {
        val layersAdapter = LayersAdapter()
        layersAdapter.items = DEFAULT_LAYERS_LIST
        layersAdapter.listener = onLayerSelectedListener
        binding.layersRecyclerView.adapter = layersAdapter
    }

    companion object {
        private val DEFAULT_LAYERS_LIST = listOf(
            LayerItem(
                ChartLayer.TOP,
                R.string.instrument_panel_layers_bring_to_top,
                R.drawable.ic_toolbar_bring_to_top
            ),
            LayerItem(
                ChartLayer.UP,
                R.string.instrument_panel_layers_bring_forward,
                R.drawable.ic_toolbar_bring_forward
            ),
            LayerItem(
                ChartLayer.BACK,
                R.string.instrument_panel_layers_send_backward,
                R.drawable.ic_toolbar_send_backward
            ),
            LayerItem(
                ChartLayer.BOTTOM,
                R.string.instrument_panel_layers_send_to_bottom,
                R.drawable.ic_toolbar_send_to_bottom
            )
        )
    }
}
