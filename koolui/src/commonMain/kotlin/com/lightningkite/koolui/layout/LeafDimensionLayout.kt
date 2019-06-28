package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.max

class LeafDimensionLayout(
        val measureLambda: (output: Measurement) -> Unit
) : BaseDimensionLayout() {

    constructor(startMargin: Float, size: Float, endMargin: Float):this({
        it.startMargin = startMargin
        it.size = size
        it.endMargin = endMargin
    })

    override fun measure(output: Measurement) {
        measureLambda(output)
    }
    override fun layoutChildren(size: Float) {}
    override val childSequence: Sequence<DimensionLayout> get() = emptySequence()

}