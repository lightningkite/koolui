package com.lightningkite.koolui.layout

interface ViewAdapter<S: V, V> {
    val view: S
    val viewAsBase: V get() = view
    fun updatePlacementX(start: Float, end: Float)
    fun updatePlacementY(start: Float, end: Float)
    fun onAddChild(layout: Layout<*, V>){}
    fun onRemoveChild(layout: Layout<*, V>){}
}

