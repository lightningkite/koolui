package com.lightningkite.koolui.layout

import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.recktangle.Rectangle

class Layout(val viewAdapter: ViewAdapter, x: DimensionCalculator, y: DimensionCalculator) {

    companion object

    val lifecycle = TreeObservableProperty()
    var parent: Layout? = null

    var x: DimensionCalculator = x
        set(value) {
            field = value
            field.dimensionAccess = xAccess
        }
    var y: DimensionCalculator = y
        set(value) {
            field = value
            field.dimensionAccess = yAccess
        }

    val xAccess: DimensionAccess = object : DimensionAccess {
        override fun updatePlacement(start: Float, end: Float) = viewAdapter.updatePlacementX(start, end)
        override val parent: DimensionCalculator? get() = this@Layout.parent?.x
    }
    val yAccess: DimensionAccess = object : DimensionAccess {
        override fun updatePlacement(start: Float, end: Float) = viewAdapter.updatePlacementY(start, end)
        override val parent: DimensionCalculator? get() = this@Layout.parent?.y
    }

    init {
        x.dimensionAccess = xAccess
        y.dimensionAccess = yAccess
    }

    fun layout(rectangle: Rectangle) {
        x.layout(rectangle.left, rectangle.right)
        y.layout(rectangle.top, rectangle.bottom)
    }

    fun invalidate() {
        x.invalidate()
        y.invalidate()
    }

    fun addChild(layout: Layout) {
        layout.parent = this
        lifecycle.parent = this.lifecycle
    }

    fun removeChild(layout: Layout) {
        layout.parent = null
        lifecycle.parent = null
    }
}