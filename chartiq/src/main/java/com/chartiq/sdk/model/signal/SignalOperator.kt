package com.chartiq.sdk.model.signal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang.StringEscapeUtils

@Parcelize
enum class SignalOperator(val key: String) : Parcelable {
    GREATER_THAN("greater_than"),
    LESS_THAN("less_than"),
    EQUAL_TO("equal_to"),
    CROSSES("crosses"),
    CROSSES_ABOVE("crosses_above"),
    CROSSES_BELOW("crosses_below"),
    TURNS_UP("turns_up"),
    TURNS_DOWN("turns_down"),
    INCREASES("increases"),
    DECREASES("decreases"),
    DOES_NOT_CHANGE("does_not_change");

    companion object {
        fun from(title: String): SignalOperator {
            return when (StringEscapeUtils.unescapeJava("\\" + title)) {
                ">" -> GREATER_THAN
                "<" -> LESS_THAN
                "=" -> EQUAL_TO
                "x" -> CROSSES
                "x+" -> CROSSES_ABOVE
                "x-" -> CROSSES_BELOW
                "t+" -> TURNS_UP
                "t-" -> TURNS_DOWN
                ">p" -> INCREASES
                "<p" -> DECREASES
                "=p" -> DOES_NOT_CHANGE
                else -> DOES_NOT_CHANGE
            }
        }
    }
}