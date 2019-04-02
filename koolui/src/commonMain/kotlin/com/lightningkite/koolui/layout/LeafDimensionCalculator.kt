package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class LeafDimensionCalculator(val startMargin: Float, val size: Float, val endMargin: Float) : BaseDimensionCalculator() {
    override fun measure(output: Measurement) {
        output.startMargin = startMargin
        output.size = size
        output.endMargin = endMargin
    }

    override fun layoutChildren(size: Float) {}

}

