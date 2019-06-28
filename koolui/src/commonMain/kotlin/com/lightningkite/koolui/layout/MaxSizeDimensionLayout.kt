package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.min

class MaxSizeDimensionLayout(
        val wraps: DimensionLayout,
        val maxSize: Float
) : DimensionLayout by wraps {
    val myMeasurement = Measurement()
    override val measurement: Measurement
        get() {
            myMeasurement.startMargin = wraps.measurement.startMargin
            myMeasurement.endMargin = wraps.measurement.endMargin
            myMeasurement.size = min(maxSize, wraps.measurement.size)
            return myMeasurement
        }
}