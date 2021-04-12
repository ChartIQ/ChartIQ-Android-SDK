package com.chartiq.demo.ui.chart.interval.model

import android.os.Parcelable
import com.chartiq.sdk.model.TimeUnit
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Interval(
    val period: Int,
    val interval: Int,
    val timeUnit: TimeUnit
) : Parcelable {

    fun getInterval(): String {
        return "$interval"
    }

    fun getSafeTimeUnit(): TimeUnit {
        return if (timeUnit == TimeUnit.HOUR) {
            TimeUnit.MINUTE
        } else {
            timeUnit
        }
    }

    companion object {
        fun createInterval(period: Int, interval: String, timeUnit: String): Interval {
            val unit: TimeUnit?
            val convertedInterval: Int
            if (timeUnit == "null") {
                val value = interval.substring(1, interval.length - 1).toUpperCase(Locale.ROOT)
                unit = TimeUnit.valueOf(value)
                convertedInterval = 1
            } else {
                convertedInterval = interval.toInt()
                unit = if(convertedInterval * period % 60 == 0) {
                    TimeUnit.HOUR
                } else {
                    TimeUnit.valueOf(timeUnit.toUpperCase(Locale.ROOT))
                }
            }
            return Interval(period, convertedInterval, unit)
        }
    }
}
