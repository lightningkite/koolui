package com.lightningkite.koolui.layout.views

import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.property.ObservableProperty

interface LayoutViewWrapper<VIEW> {
    fun nativeViewAdapter(wraps: Layout<*, VIEW>): VIEW
    fun defaultViewContainer(): VIEW
    fun <SPECIFIC : VIEW> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, VIEW>
    fun intrinsicDimensionLayouts(view: VIEW): LeafDimensionLayouts
    fun applyEntranceTransition(view: VIEW, animation: Animation)
    fun applyExitTransition(view: VIEW, animation: Animation, onComplete: () -> Unit)
}

fun <SPECIFIC: VIEW, VIEW> LayoutViewWrapper<VIEW>.wrap(view: SPECIFIC): Layout<SPECIFIC, VIEW> {
    val dim = intrinsicDimensionLayouts(view)
    return Layout(
            viewAdapter = view.adapter(),
            x = dim.x,
            y = dim.y
    )
}
inline fun <SPECIFIC: VIEW, VIEW> LayoutViewWrapper<VIEW>.wrap(view: SPECIFIC, configure: SPECIFIC.(lifecycle: TreeObservableProperty)->Unit): Layout<SPECIFIC, VIEW> = wrap(view).apply {
    view.configure(this.isAttached)
}
inline fun <SPECIFIC: VIEW, VIEW> LayoutViewWrapper<VIEW>.intrinsicLayout(view: SPECIFIC, configure: SPECIFIC.(Layout<SPECIFIC, VIEW>)->Unit): Layout<SPECIFIC, VIEW> = wrap(view).apply {
    view.configure(this)
}