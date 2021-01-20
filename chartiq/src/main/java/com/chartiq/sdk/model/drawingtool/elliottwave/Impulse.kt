package com.chartiq.sdk.model.drawingtool.elliottwave

/**
 * An enumeration of available impulses
 */
enum class Impulse(val value: String) {
    ROMAN_CAPITAL("I II III IV V"),
    ROMAN_NORMAL("i ii iii iv v"),
    ARABIC_NORMAL("1 2 3 4 5"),
    ABCDE_CAPITAL("A B C D E"),
    ABCDE_NORMAL("a b c d e"),
    WXYXZ_CAPITAL("W X Y X Z"),
    WXYXZ_NORMAL("w x y x z"),
    NONE("- - -")
}
