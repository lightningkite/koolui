package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class PassOnDimensionLayout(
        val child: DimensionLayout
) : BaseDimensionLayout() {

    init {
        child.parent = this
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = sequenceOf(child)

    override fun measure(output: Measurement) {
        output.set(child.measurement)
    }

    override fun layoutChildren(size: Float) {
        child.layout(0f, size)
    }
}