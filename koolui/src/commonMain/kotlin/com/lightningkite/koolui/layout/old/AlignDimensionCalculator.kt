package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.Measurement

class AlignDimensionCalculator(
        val children: Sequence<Pair<Align, DimensionCalculator>>
) : BaseDimensionCalculator() {

    override fun measure(output: Measurement) {
        output.startMargin = children.map { it.second.measurement.startMargin }.min() ?: 0f
        output.endMargin = children.map { it.second.measurement.endMargin }.min() ?: 0f
        output.size = children.map {
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

}