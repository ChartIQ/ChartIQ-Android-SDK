package com.chartiq.sdk.model.charttype

/**
 * A list of possible aggregation chart types
 * @property value A String value of the type
 */
enum class ChartAggregationType(
    /**
     * @suppress
     */
    val value: String
) {
    /**
     * <p>Heikin-Ashi charts are time series charts that resemble candle charts. In a normal candle chart,
     * each candle is calculated independent of the other candles. However in Heikin-Ashi charts, the candles appear to
     * link together as a consequence of how they are calculated. The candles are calculated as such:</p>
     * <ul>
     *     <li>Open = the mean of the previous open and the previous close</li>
     *     <li>Close = the mean of the current open, close, high, and low</li>
     *     <li>High = the maximum of the current high, open and close</li>
     *     <li>High = the maximum of the current high, open and close</li>
     * </ul>
     */
    HEIKINASHI("Heikin Ashi"),

    /**
     * Kagi charts appear as vertical bars connected by small horizontal segments at right angles.
     * These charts are independent of time and progress forward based on price action. Thick green bars, called ‘Yang’ bars,
     * indicate that a price has broken-out above the previous high price. Thin red bars, called ‘Yin’ bars,
     * indicate that the price has fallen below the previous low. Unlike the other chart types, the colors of kagi
     * lines do not directly communicate upward or downward trends.
     */
    KAGI("Kagi"),

    /**
     * <p>Line break charts appear as vertical bars that ascend and descend. These charts are independent of time and are
     * determined only by price action. Ascending bars are colored green and indicate upward price action.
     * Descending bars are colored red, and indicate downward price action.</p>
     * <p>Line break charts are constructed by looking at the close of a bar and comparing it to a previous bar’s close
     * (which bar to compare is determined by the user, see below). If the current bar’s close is higher than the one
     * it is being compared to, a green ascending bar is drawn. If the current bar’s close is lower than the one that
     * it is being compared to, a red descending bar is drawn. If the current close is the same, or if the price does
     * not move enough in one direction or the other to signify a reversal, then no bar is drawn.</p>
     */
    LINEBREAK("Line Break"),

    /**
     * <p>Renko charts appear as a sequence of uniformly sized bars (referred to as ‘bricks’) that connect at their corners.
     * These charts are independent of time and are determined only by price action. Ascending bricks are colored green,
     * and descending bricks are colored red.</p>
     */
    RENKO("Renko"),

    /**
     * <p>Range bars appear as a sequence of uniformly sized bars that connect at their corners and sometimes sit
     * adjacent to one another. These charts are independent of time and are determined only by price action.
     * Ascending bars are colored green, and descending bars are colored red.</p>
     * <p>Range bars are constructed by assigning a ‘range’ value for each bar to represent. A new bar is drawn when the
     * price moves above or below the amount signified by the previous range bar. Ascending bars adjacent to descending
     * bars indicates price oscillation between the high and low of the bars. Bars obey three rules of construction:</p>
     * <ul>
     *     <li>Bars extend the length of the specified range.</li>
     *     <li>Bars open at the close of the previous bar.</li>
     *     <li>Bars must close at either the high or low value.</li>
     * </ul>
     */
    RANGEBARS("Range Bars"),

    /**
     * <p>Point &amp; figure (sometimes shortened to P&amp;F or PnF) charts appear as alternating columns of X’s and O’s.
     * These charts are independent of time and are determined only by price action. The X columns indicate upward price
     * action, and are colored green. The O columns indicate downward price action, and are colored red.</p>
     */
    PANDF("Point & Figure")
}
