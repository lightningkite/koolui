package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.max

class LinearDimensionLayout(
        val children: List<Pair<Float, DimensionLayout>>
) : BaseDimensionLayout() {
    init {
        children.forEach { it.second.parent = this }
    }

    val childToAlign = children.associate { it.second to it.first }

    override fun measure(output: Measurement) {
        val asList = children.toList()
        output.startMargin = asList.firstOrNull()?.second?.measurement?.startMargin ?: 0f
        output.endMargin = asList.lastOrNull()?.second?.measurement?.endMargin ?: 0f
        output.size = children.zipWithNext().map {
            max(it.first.second.measurement.endMargin, it.second.second.measurement.startMargin)
        }.sum() + children.map { it.second.measurement.size }.sum()
    }

    override fun layoutChildren(size: Float) {
        val measurementWithoutWeighted = children.zipWithNext().map {
            max(it.first.second.measurement.endMargin, it.second.second.measurement.startMargin)
        }.sum() + children.filter { it.first == 0f }.map { it.second.measurement.size }.sum()

        val spaceToDistribute = size - measurementWithoutWeighted
        val distributionDivisor = children.map { it.first }.sum().coerceAtLeast(1f)
        var position = 0f
        var previousRequestedSpace = -1f
        for ((weight, child) in children) {
            //add spacing
            if (previousRequestedSpace != -1f) {
                position += max(previousRequestedSpace, child.measurement.startMargin)
            }

            val subsize = if (weight == 0f)
                child.measurement.size
            else
                (weight / distributionDivisor) * spaceToDistribute

            child.layout(position, position + subsize)
            position += subsize

            previousRequestedSpace = child.measurement.endMargin
        }
    }

    override val childSequence: Sequence<DimensionLayout>
        get() = children.asSequence().map { it.second }
}