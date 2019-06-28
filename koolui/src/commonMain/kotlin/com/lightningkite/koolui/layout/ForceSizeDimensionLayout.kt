package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class ForceSizeDimensionLayout(
        val wraps: DimensionLayout,
        val size: Float
) : DimensionLayout by wraps {
    val myMeasurement = Measurement()
    override val measurement: Measurement
        get() {
            myMeasurement.startMargin = wraps.measurement.startMargin
            myMeasurement.endMargin = wraps.measurement.endMargin
            myMeasurement.size = size
            return myMeasurement
        }
}