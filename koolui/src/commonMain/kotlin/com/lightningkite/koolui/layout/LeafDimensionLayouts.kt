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
//            println("${this@LeafDimensionLayouts}: Y Invalidated due to X change for leaf (size: $size)")
        }

        override fun layout(start: Float, end: Float): Boolean {
            val result = super.layout(start, end)
//            println("${this@LeafDimensionLayouts}: X laid out for leaf: ${size}")
            return result
        }

        override fun measure(output: Measurement) {
            measureX(output)
//            println("${this@LeafDimensionLayouts}: X Measured for leaf: ${output}")
        }
    }
    val y: DimensionLayout = object : BaseDimensionLayout() {
        override val childSequence: Sequence<DimensionLayout>
            get() = emptySequence()

        override fun layoutChildren(size: Float) {}

        override fun layout(start: Float, end: Float): Boolean {
            val result = super.layout(start, end)
//            println("${this@LeafDimensionLayouts}: Y laid out for leaf: ${size}")
            return result
        }

        override fun measure(output: Measurement) {
//            println("${this@LeafDimensionLayouts}: Y Measured for leaf given x=${x.size}: ${output}")
            measureY(x.size, output)
        }
//        override fun measure(layout: BaseDimensionLayout, output: Measurement) = measureY(myXLayout.size, output)
    }
}

