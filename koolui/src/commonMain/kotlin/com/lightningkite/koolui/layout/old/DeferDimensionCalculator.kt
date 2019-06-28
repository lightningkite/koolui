package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Measurement

class DeferDimensionCalculator(
        val child: () -> DimensionCalculator
) : BaseDimensionCalculator() {

    override fun measure(output: Measurement) {
        output.set(child().measurement)
    }

    override fun layoutChildren(size: Float) {
        val c = child()
        c.layout(0f, size)
    }

}

