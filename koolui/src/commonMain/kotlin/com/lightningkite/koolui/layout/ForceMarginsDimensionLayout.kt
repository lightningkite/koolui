package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class ForceMarginsDimensionLayout(
        val wraps: DimensionLayout,
        val startMargin: Float,
        val endMargin: Float
) : DimensionLayout by wraps {
    val myMeasurement = Measurement()
    override val measurement: Measurement
        get() {
            myMeasurement.startMargin = startMargin
            myMeasurement.endMargin = endMargin
            myMeasurement.size = wraps.measurement.size
            return myMeasurement
        }
}