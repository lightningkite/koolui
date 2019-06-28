package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.Measurement

class FrameDimensionCalculator(
        val startMargin: Float = 8f,
        val endMargin: Float = 8f,
        val child: () -> DimensionCalculator
) : BaseDimensionCalculator() {

    override fun measure(output: Measurement) {
        output.startMargin = startMargin
        output.endMargin = endMargin
        output.size = child().measurement.totalSpace
    }

    override fun layoutChildren(size: Float) {
        val c = child()
        c.layout(c.measurement.startMargin, size - c.measurement.endMargin)
    }

}