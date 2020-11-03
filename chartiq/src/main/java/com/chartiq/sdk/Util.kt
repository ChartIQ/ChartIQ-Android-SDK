package com.chartiq.sdk

import android.content.Context
import android.util.TypedValue
import com.google.gson.Gson


fun buildArgumentStringFromArgs(vararg args: Any?): String =
    Gson().toJson(args).apply {
        substring(1, length - 1)
    }

