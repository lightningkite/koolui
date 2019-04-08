package com.lightningkite.koolui.layout

import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.recktangle.Rectangle

class Layout<out S : V, V>(val viewAdapter: ViewAdapter<S, V>, x: DimensionCalculator, y: DimensionCalculator) {

    companion object

    val isAttached = TreeObservableProperty()
    var parent: Layout<*, V>? = null

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

    fun addChild(layout: Layout<*, V>) {
        layout.parent = this
        isAttached.parent = this.isAttached
    }

    fun removeChild(layout: Layout<*, V>) {
        layout.parent = null
        isAttached.parent = null
    }
}
