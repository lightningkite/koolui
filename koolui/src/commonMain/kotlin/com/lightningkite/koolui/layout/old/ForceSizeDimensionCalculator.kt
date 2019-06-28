package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Measurement

class ForceSizeDimensionCalculator(val underlying: DimensionCalculator, val forceSize: Float) : DimensionCalculator by underlying {
    override val measurement: Measurement
        get() = underlying.measurement.apply {
            this.size = forceSize
        }
}

