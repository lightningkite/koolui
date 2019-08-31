package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.max
import kotlin.math.min

class ScrollDimensionLayout(
        val wraps: DimensionLayout
) : BaseDimensionLayout() {
    init {
        wraps.parent = this
    }

    override fun measure(output: Measurement) {
        output.set(wraps.measurement)
    }

    override fun layoutChildren(size: Float) {
        wraps.layout(0f, min(size, wraps.measurement.size))
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = sequenceOf(wraps)
}