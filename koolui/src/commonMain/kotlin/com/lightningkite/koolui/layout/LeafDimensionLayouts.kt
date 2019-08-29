package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

abstract class LeafDimensionLayouts {
    abstract fun measureX(output: Measurement)
    abstract fun measureY(xSize: Float, output: Measurement)

    val x: DimensionLayout = object : BaseDimensionLayout() {
        override val childSequence: Sequence<DimensionLayout>
            get() = emptySequence()

        override fun layoutChildren(size: Float) {
            y.requestMeasurement()
            y.requestLayout()
        }

        override fun layout(start: Float, end: Float): Boolean {
            val result = super.layout(start, end)
            return result
        }

        override fun measure(output: Measurement) {
            measureX(output)
        }
    }
    val y: DimensionLayout = object : BaseDimensionLayout() {
        override val childSequence: Sequence<DimensionLayout>
            get() = emptySequence()

        override fun layoutChildren(size: Float) {}

        override fun layout(start: Float, end: Float): Boolean {
            val result = super.layout(start, end)
            return result
        }

        override fun measure(output: Measurement) {
            measureY(x.size, output)
        }
    }
}

