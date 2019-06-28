package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.recktangle.Rectangle

class Layout<out S : V, V>(val viewAdapter: ViewAdapter<S, V>, x: DimensionCalculator, y: DimensionCalculator) {

    companion object

    val isAttached = TreeObservableProperty()
    var parent: Layout<*, V>? = null

    init {
        isAttached.add {
            if(it){
                println("Enabled: ${viewAdapter.view?.let{(it as Any)::class}}")
            } else {
                println("Disabled: ${viewAdapter.view?.let{(it as Any)::class}}")
            }
        }
    }

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
        x.refresh()
        y.refresh()
    }

    fun addChild(layout: Layout<*, V>) {
        viewAdapter.onAddChild(layout)
        layout.parent = this
        layout.isAttached.parent = this.isAttached
    }

    fun removeChild(layout: Layout<*, V>) {
        viewAdapter.onRemoveChild(layout)
        layout.parent = null
        layout.isAttached.parent = null
    }
}
