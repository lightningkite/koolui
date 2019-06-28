package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Measurement

class LeafDimensionCalculator(var startMargin: Float, var size: Float, var endMargin: Float) : BaseDimensionCalculator() {
    override fun measure(output: Measurement) {
        output.startMargin = startMargin
        output.size = size
        output.endMargin = endMargin
    }

    override fun layoutChildren(size: Float) {}

}

