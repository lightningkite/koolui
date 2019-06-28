package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

interface DimensionLayout {
    fun childNeedsMeasurement(fromChild: DimensionLayout)
    fun childNeedsLayout(fromChild: DimensionLayout? = null)

    var parent: DimensionLayout?
    val measurement: Measurement
    /**
     * @return Whether or not any work had to be done.
     */
    fun layout(start: Float, end: Float): Boolean
    /**
     * @return Whether or not any work had to be done.
     */
    fun refresh(): Boolean

    val start: Float
    val end: Float

    val needsLayout: Boolean
    val childNeedsLayout: Boolean

    fun requestMeasurement()
    fun requestLayout()

    var onPlacement: (start: Float, end: Float) -> Unit
    var onLayoutRequest: ()->Unit
}


val DimensionLayout.size: Float get() = end - start