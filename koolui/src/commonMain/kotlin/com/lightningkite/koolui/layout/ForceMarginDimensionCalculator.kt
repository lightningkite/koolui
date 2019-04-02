package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class ForceMarginDimensionCalculator(val underlying: DimensionCalculator, val forceStartMargin: Float, val forceEndMargin: Float) : DimensionCalculator by underlying {
    override val measurement: Measurement
        get() = underlying.measurement.apply {
            this.startMargin = forceStartMargin
            this.endMargin = forceEndMargin
        }
}