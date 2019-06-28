package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

class SwapDimensionLayout(
        child: DimensionLayout,
        val startMargin: Float = 0f,
        val endMargin: Float = 0f
) : BaseDimensionLayout() {

    var child: DimensionLayout
        get() = layoutChild
        set(value){
            layoutChild = value
            measurementChild = value
        }

    var layoutChild: DimensionLayout = child
        set(value){
            if(field != measurementChild) {
                field.parent = null
            }
            field = value
            value.parent = this
            requestLayout()
        }

    var measurementChild: DimensionLayout = child
        set(value){
            if(field != layoutChild) {
                field.parent = null
            }
            field = value
            value.parent = this
            requestMeasurement()
        }

    init {
        child.parent = this
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = sequenceOf(layoutChild, measurementChild)

    override fun measure(output: Measurement) {
        output.startMargin = startMargin
        output.endMargin = endMargin
        output.size = layoutChild.measurement.totalSpace
    }

    override fun layoutChildren(size: Float) {
        layoutChild.layout(layoutChild.measurement.startMargin, size - layoutChild.measurement.endMargin)
    }

    override fun childInvalidatesLayout(fromChild: DimensionLayout): Boolean = fromChild == layoutChild
    override fun childInvalidatesMeasurement(fromChild: DimensionLayout): Boolean = fromChild == measurementChild
}