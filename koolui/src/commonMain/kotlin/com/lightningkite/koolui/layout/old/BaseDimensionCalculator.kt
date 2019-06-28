package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.Measurement

abstract class BaseDimensionCalculator : DimensionCalculator {
    override var dimensionAccess: DimensionAccess = DimensionAccess.None

    abstract fun measure(output: Measurement)
    var dirtyMeasurement = true
    private val currentMeasurement = Measurement()
    override val measurement: Measurement
        get() {
            if (dirtyMeasurement) {
                dirtyMeasurement = false
                measure(currentMeasurement)
            }
            return currentMeasurement
        }

    abstract fun layoutChildren(size: Float)
    private var lastSize: Float = 0f
    fun layoutIfSizeChanged(size: Float) {
        if (size != lastSize) {
            lastSize = size
            layoutChildren(size)
        }
    }

    override fun layout(start: Float, end: Float) {
        dimensionAccess.updatePlacement(start, end)
        layoutIfSizeChanged(end - start)
    }

    private val lastMeasurement: Measurement = Measurement()
    override fun refresh() {
        dirtyMeasurement = true
        if (lastMeasurement != measurement) {
            lastMeasurement.set(measurement)
            //If our size changed, we need to inform our parent to relayout.
            dimensionAccess.parent?.let {
                //Force full relayout of this thing when coming back down the chain later
                lastSize = -1f
                //Tell the parent to do this same thing
                it.refresh()
            } ?: run {
                layoutChildren(lastSize)
            }
        } else {
            //Force relayout of this view
            layoutChildren(lastSize)
        }
    }
}