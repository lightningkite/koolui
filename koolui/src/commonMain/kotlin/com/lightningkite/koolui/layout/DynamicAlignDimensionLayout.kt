package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.Measurement

class DynamicAlignDimensionLayout(
        children: List<Pair<Align, DimensionLayout>>
) : BaseDimensionLayout() {

    val privateChildren = children.toMutableList()
    val children: List<Pair<Align, DimensionLayout>> get() = privateChildren

    init {
        children.forEach { it.second.parent = this }
    }

    val childToAlign = children.associate { it.second to it.first }

    fun addChild(child: Pair<Align, DimensionLayout>, index: Int = children.lastIndex) {
        privateChildren.add(index, child)
        requestMeasurement()
        requestLayout()
    }

    fun removeChild(child: DimensionLayout) {
        privateChildren.removeAll {
            val result = it.second == child
            if (result) {
                it.second.parent = null
            }
            result
        }
        requestMeasurement()
        requestLayout()
    }

    override fun childInvalidatesLayout(fromChild: DimensionLayout): Boolean = childToAlign[fromChild] != Align.Fill

    override fun measure(output: Measurement) {
        output.startMargin = children.asSequence().map { it.second.measurement.startMargin }.min() ?: 0f
        output.endMargin = children.asSequence().map { it.second.measurement.endMargin }.min() ?: 0f
        output.size = children.asSequence().map {
            it.second.measurement.size +
                    (it.second.measurement.startMargin - output.startMargin).coerceAtLeast(0f) +
                    (it.second.measurement.endMargin - output.endMargin).coerceAtLeast(0f)
        }.max() ?: 0f
    }

    override fun layoutChildren(size: Float) {
        children.forEach { (align, child) ->
            when (align) {
                Align.Start -> {
                    val start = child.measurement.startMargin - measurement.startMargin
                    child.layout(
                            start = start,
                            end = start + child.measurement.size
                    )
                }
                Align.Center -> {
                    child.layout(
                            start = (size - child.measurement.size) / 2,
                            end = (size + child.measurement.size) / 2
                    )
                }
                Align.End -> {
                    val end = size + measurement.endMargin - child.measurement.endMargin
                    child.layout(
                            start = end - child.measurement.size,
                            end = end
                    )
                }
                Align.Fill -> {
                    child.layout(
                            start = child.measurement.startMargin - measurement.startMargin,
                            end = size - (child.measurement.endMargin - measurement.endMargin)
                    )
                }
            }
        }
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = children.asSequence().map { it.second }
}