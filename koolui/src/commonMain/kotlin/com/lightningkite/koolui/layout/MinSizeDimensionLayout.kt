package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.max

class MinSizeDimensionLayout(
        val wraps: DimensionLayout,
        val minSize: Float
) : DimensionLayout by wraps {
    val myMeasurement = Measurement()
    override val measurement: Measurement
        get() {
            myMeasurement.startMargin = wraps.measurement.startMargin
            myMeasurement.endMargin = wraps.measurement.endMargin
            myMeasurement.size = max(minSize, wraps.measurement.size)
            return myMeasurement
        }
}