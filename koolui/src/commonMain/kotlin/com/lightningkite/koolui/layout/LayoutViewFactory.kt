package com.lightningkite.koolui.layout

import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.layout.space
import com.lightningkite.reacktive.property.*
import com.lightningkite.recktangle.Point

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
abstract class LayoutViewFactory<VIEW>(var root: Layout<*, VIEW>? = null) : ViewFactory<Layout<*, VIEW>> {

    abstract fun defaultViewContainer(): VIEW
    abstract fun <SPECIFIC : VIEW> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, VIEW>
    abstract fun applyEntranceTransition(view: VIEW, animation: Animation)
    abstract fun applyExitTransition(view: VIEW, animation: Animation, onComplete: () -> Unit)

    override fun contentRoot(view: Layout<*, VIEW>): Layout<*, VIEW> {
        view.isAttached.alwaysOn = true
        val result = Layout(
                viewAdapter = defaultViewContainer().adapter(),
                x = DynamicAlignDimensionLayout(listOf(Align.Fill to view.x)),
                y = DynamicAlignDimensionLayout(listOf(Align.Fill to view.y))
        )
        result.addChild(view)
        this.root = result
        return result
    }

    override fun launchDialog(dismissable: Boolean, onDismiss: () -> Unit, makeView: (dismissDialog: () -> Unit) -> Layout<*, VIEW>) {
        val root = root ?: return
        val x = root.x as? DynamicAlignDimensionLayout ?: return
        val y = root.y as? DynamicAlignDimensionLayout ?: return

        var dismiss: () -> Unit = {}
        val newView = align(
                AlignPair.FillFill to space(100f).background(colorSet.backgroundDisabled.copy(alpha = .5f)).clickable { dismiss() }.margin(0f),
                AlignPair.CenterCenter to makeView { dismiss() }
        )

        root.addChild(newView)
        x.addChild(Align.Fill to newView.x)
        y.addChild(Align.Fill to newView.y)
        applyEntranceTransition(newView.viewAsBase, Animation.Fade)

        dismiss = {
            applyExitTransition(newView.viewAsBase, Animation.Fade) {
                x.removeChild(newView.x)
                y.removeChild(newView.y)
                root.removeChild(newView)
            }
        }
    }

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
