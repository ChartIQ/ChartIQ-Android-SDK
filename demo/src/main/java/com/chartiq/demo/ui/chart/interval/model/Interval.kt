package com.chartiq.demo.ui.chart.interval.model

import android.os.Parcelable
import com.chartiq.sdk.model.TimeUnit
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Interval(
    val duration: Int,
    val timeUnit: TimeUnit
) : Parcelable {
    fun getPeriod(): Int {
        return if (timeUnit == TimeUnit.HOUR) {
            duration * 2
        } else {
            1
        }
    }

    fun getInterval(): String {
        val interval = if (timeUnit == TimeUnit.HOUR) {
            30
        } else {
            duration
        }
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
        fun createInterval(periodicity: Int, interval: String, timeUnit: String): Interval {
            val unit: TimeUnit?
            val duration: Int
            if (timeUnit == "null") {
                val value = interval.substring(1, interval.length - 1).toUpperCase(Locale.ROOT)
                unit = TimeUnit.valueOf(value)
                duration = periodicity
            } else if (periodicity > 1 && timeUnit.toUpperCase(Locale.ROOT) == TimeUnit.MINUTE.toString()) {
                unit = TimeUnit.HOUR
                duration = periodicity * interval.toInt() / 60
            } else {
                unit = TimeUnit.valueOf(timeUnit.toUpperCase(Locale.ROOT))
                duration = interval.toInt()
            }
            return Interval(duration, unit)
        }
    }
}
