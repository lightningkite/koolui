package com.lightningkite.koolui.layout

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
        layoutIfSizeChanged(end - start)
        dimensionAccess.updatePlacement(start, end)
    }

    private val lastMeasurement: Measurement = Measurement()
    override fun invalidate() {
        dirtyMeasurement = true
        if (lastMeasurement != measurement) {
            lastMeasurement.set(measurement)
            //If our size changed, we need to inform our parent to relayout.
            lastSize = -1f //Force full relayout of this thing when coming back down the chain later
            dimensionAccess.parent?.invalidate() //Tell the parent to do this same thing
        } else {
            //Force relayout of this view
            layoutChildren(lastSize)
        }
    }
}