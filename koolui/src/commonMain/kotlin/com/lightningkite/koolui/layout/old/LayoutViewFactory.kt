package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.reacktive.property.*

/**
 * PHILOSOPHY
 *
 * The views will function and appear according to the underlying platform.  Styling does
 * not take place at this interface layer.
 *
 * Thus, every function here is meant to represent a concept rather than a specific widget.
 *
 * This interface is meant to be extended and added upon, and only represents the most basic of
 * views needed for creating an app.
 *
 * Layout does take place at this layer, and is meant to be good at resizing.
 *
 * All views are automatically sized unless stated otherwise - either shrinking as small as possible
 * or growing as large as possible.
 *
 * The defaults for spacing are set to look both clean and good - modify them deliberately.
 *
 * The returned view objects are only meant to be used in composing with other views in the factory.
 * Do not attempt to store references to them long-term or anything of the sort.
 */
abstract class LayoutViewFactory<VIEW>: ViewFactory<Layout<*, VIEW>> {

    abstract fun defaultViewContainer(): VIEW
    abstract fun <SPECIFIC: VIEW> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, VIEW>

    override fun horizontal(vararg views: Pair<LinearPlacement, Layout<*, VIEW>>): Layout<*, VIEW> = Layout.horizontal(
            viewAdapter = defaultViewContainer().adapter(),
            children = views.toList()
    )

    override fun vertical(vararg views: Pair<LinearPlacement, Layout<*, VIEW>>): Layout<*, VIEW> = Layout.vertical(
            viewAdapter = defaultViewContainer().adapter(),
            children = views.toList()
    )

    override fun frame(view: Layout<*, VIEW>): Layout<*, VIEW> = Layout.frame(
            viewAdapter = defaultViewContainer().adapter(),
            child = view
    )

    override fun align(vararg views: Pair<AlignPair, Layout<*, VIEW>>): Layout<*, VIEW> = Layout.align(
            viewAdapter = defaultViewContainer().adapter(),
            children = views.toList()
    )

    override fun swap(view: ObservableProperty<Pair<Layout<*, VIEW>, Animation>>): Layout<*, VIEW> = Layout.swap(
            viewAdapter = defaultViewContainer().adapter(),
            child = view.transform { it.first }
    )

    override fun Layout<*, VIEW>.margin(left: Float, top: Float, right: Float, bottom: Float): Layout<*, VIEW> {
        this.forceXMargins(left, right)
        this.forceYMargins(top, bottom)
        return this
    }

    override fun Layout<*, VIEW>.setWidth(width: Float): Layout<*, VIEW> {
        this.forceWidth(width)
        return this
    }

    override fun Layout<*, VIEW>.setHeight(height: Float): Layout<*, VIEW> {
        this.forceHeight(height)
        return this
    }

    override val Layout<*, VIEW>.lifecycle: ObservableProperty<Boolean>
        get() = this.isAttached
}
