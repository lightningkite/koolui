package com.lightningkite.koolui.layout.views

import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.layout.*
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.dialogs.ViewFactoryDialogs
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.layout.space
import com.lightningkite.koolui.views.root.ViewFactoryRoot
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point


interface LayoutVFLayout<VIEW> :
        ViewFactoryLayout<Layout<*, VIEW>>,
        LayoutViewWrapper<VIEW>
{

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


    override fun swap(view: ObservableProperty<Pair<Layout<*, VIEW>, Animation>>, staticViewForSizing: Layout<*, VIEW>?): Layout<*, VIEW> =
            if (staticViewForSizing == null) Layout.swap(
                    viewAdapter = defaultViewContainer().adapter(),
                    child = view,
                    applyEntranceTransition = { view, animation -> applyEntranceTransition(view, animation) },
                    applyExitTransition = { view, animation, onComplete -> applyExitTransition(view, animation, onComplete) }
            )
            else Layout.swapStatic(
                    viewAdapter = defaultViewContainer().adapter(),
                    child = view,
                    applyEntranceTransition = { view, animation -> applyEntranceTransition(view, animation) },
                    applyExitTransition = { view, animation, onComplete -> applyExitTransition(view, animation, onComplete) },
                    sizingChild = staticViewForSizing
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

    override fun space(size: Point): Layout<*, VIEW> = Layout(
            viewAdapter = defaultViewContainer().adapter(),
            x = LeafDimensionLayout(0f, size.x, 0f),
            y = LeafDimensionLayout(0f, size.y, 0f)
    )
}
