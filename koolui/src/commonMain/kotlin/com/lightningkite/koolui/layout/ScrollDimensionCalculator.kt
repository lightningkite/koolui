package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.max

class ScrollDimensionCalculator(val fillContent: Boolean = true, val child: ()->DimensionCalculator) : BaseDimensionCalculator() {
    override fun measure(output: Measurement) {
        output.set(child().measurement)
    }

    override fun layoutChildren(size: Float) {
        child().layout(0f, max(size, measurement.size))
    }

}

