package com.chartiq.demo.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * This function creates a [LiveData] of a [Pair] of the two types provided. The resulting LiveData
 * is updated whenever either input LiveData updates and both LiveData have updated at least once before.
 * @param a the first LiveData
 * @param b the second LiveData
 */
fun <A, B> LiveData<A>.combineLatest(b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        addSource(this@combineLatest) {
            if (it == null && value != null) value = null
            lastA = it
            if (lastA != null && lastB != null) value = lastA!! to lastB!!
        }

        addSource(b) {
            if (it == null && value != null) value = null
            lastB = it
            if (lastA != null && lastB != null) value = lastA!! to lastB!!
        }
    }
}