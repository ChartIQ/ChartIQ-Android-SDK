package com.chartiq.sdk

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityManager
import android.webkit.WebView


internal class ChartIQView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs) {

    val accessibilityManager: AccessibilityManager by lazy {
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }
}
