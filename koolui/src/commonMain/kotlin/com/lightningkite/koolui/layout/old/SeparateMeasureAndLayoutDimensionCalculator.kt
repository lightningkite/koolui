package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Measurement

class SeparateMeasureAndLayoutDimensionCalculator(
        val measure: () -> DimensionCalculator,
        val layout: () -> DimensionCalculator
) : BaseDimensionCalculator() {

    override fun measure(output: Measurement) {
        output.set(measure.invoke().measurement)
    }

    override fun layoutChildren(size: Float) {
        val c = layout.invoke()
        c.layout(0f, size)
    }

}