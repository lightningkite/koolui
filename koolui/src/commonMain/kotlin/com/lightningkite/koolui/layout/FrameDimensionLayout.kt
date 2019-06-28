package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class FrameDimensionLayout(
        val child: DimensionLayout,
        val startMargin: Float = 8f,
        val endMargin: Float = 8f
) : BaseDimensionLayout() {

    init {
        child.parent = this
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = sequenceOf(child)

    override fun measure(output: Measurement) {
        output.startMargin = startMargin
        output.endMargin = endMargin
        output.size = child.measurement.totalSpace
    }

    override fun layoutChildren(size: Float) {
        child.layout(child.measurement.startMargin, size - child.measurement.endMargin)
    }
}