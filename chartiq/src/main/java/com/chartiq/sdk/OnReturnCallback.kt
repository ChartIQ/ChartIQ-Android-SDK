package com.chartiq.sdk

fun interface OnReturnCallback<T> {
    fun onReturn(result: T)
}
