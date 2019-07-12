package com.lightningkite.koolui.layout

import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.recktangle.Rectangle

class Layout<S : V, V>(
        val viewAdapter: ViewAdapter<S, V>,
        x: DimensionLayout,
        y: DimensionLayout
) {
    var x: DimensionLayout = x
        set(value){
            field.onPlacement = { _, _ -> }
            field = value
            value.onPlacement = viewAdapter::updatePlacementX
        }
    var y: DimensionLayout = y
        set(value){
            field.onPlacement = { _, _ -> }
            field = value
            value.onPlacement = viewAdapter::updatePlacementY
        }

    init {
        x.onPlacement = viewAdapter::updatePlacementX
        y.onPlacement = viewAdapter::updatePlacementY
    }

    val view: S get() = viewAdapter.view
    val viewAsBase: V get() = viewAdapter.viewAsBase

    var parent: Layout<*, V>? = null
        private set
    val isAttached = TreeObservableProperty()

    fun addSemiChild(layout: Layout<*, V>) {
        layout.parent = this
        layout.isAttached.parent = this.isAttached
    }

    fun removeSemiChild(layout: Layout<*, V>) {
        layout.parent = null
        layout.isAttached.parent = null
    }

    fun addChild(layout: Layout<*, V>) {
        layout.parent = this
        layout.isAttached.parent = this.isAttached
        viewAdapter.onAddChild(layout)
    }

    fun removeChild(layout: Layout<*, V>) {
        viewAdapter.onRemoveChild(layout)
        layout.parent = null
        layout.isAttached.parent = null
    }

    fun layout(rectangle: Rectangle){
        for(i in 1..3){
            println("--Layout iteration $i X--")
            val xDone = !x.layout(rectangle.left, rectangle.right)
            println("--Layout iteration $i Y--")
            val yDone = !y.layout(rectangle.top, rectangle.bottom)
            if(xDone && yDone) break
        }
    }

    fun refresh(){
        for(i in 1..3){
            println("--Layout iteration $i X--")
            val xDone = !x.refresh()
            println("--Layout iteration $i Y--")
            val yDone = !y.refresh()
            if(xDone && yDone) break
        }
    }

    fun requestMeasurement(){
        x.requestMeasurement()
        y.requestMeasurement()
    }

    fun requestLayout(){
        x.requestLayout()
        y.requestLayout()
    }

    companion object
}

