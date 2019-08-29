package com.lightningkite.koolui.views.layout

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.async.scope
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.*

interface ViewFactoryLayout<VIEW> {

    /**
     * Wraps content to make it scroll vertically.
     */
    fun scrollVertical(
            view: VIEW,
            amount: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW = scrollBoth(view, amountY = amount)

    /**
     * Wraps content to make it scroll horizontally.
     */
    fun scrollHorizontal(
            view: VIEW,
            amount: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW = scrollBoth(view, amountX = amount)

    /**
     * Wraps content to make it scroll both directions.
     */
    fun scrollBoth(
            view: VIEW,
            amountX: MutableObservableProperty<Float> = StandardObservableProperty(0f),
            amountY: MutableObservableProperty<Float> = StandardObservableProperty(0f)
    ): VIEW

    /**
     * Shows a single view at a time, which can be switched out with another through animation.
     * If you provide a [staticViewForSizing], the view's size will always be based on that view instead of another.
     */
    fun swap(
            view: ObservableProperty<Pair<VIEW, Animation>>,
            staticViewForSizing: VIEW? = null
    ): VIEW

    /**
     * Places elements horizontally, left to right.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun horizontal(
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW

    /**
     * Places elements vertically, top to bottom.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun vertical(
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW

    /**
     * Places elements linearly, either left to right or top to bottom.
     * The orientation is determined automatically based on what the system decides fits the elements best.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun linear(
            defaultToHorizontal: Boolean = false,
            vararg views: Pair<LinearPlacement, VIEW>
    ): VIEW = if (defaultToHorizontal) horizontal(*views) else vertical(*views)

    /**
     * Places elements on top of each other, back to front.
     * The placement pairs determine whether or not the elements are stretched or shifted around.
     */
    fun align(
            vararg views: Pair<AlignPair, VIEW>
    ): VIEW

    /**
     * Frames these elements separately.
     */
    fun frame(
            view: VIEW
    ): VIEW = align(AlignPair.FillFill to view)

    /**
     * Adds a 'card' background to the given item.
     */
    fun card(view: VIEW): VIEW// = frame(view).margin(8f).background(colorSet.backgroundHighlighted)

    /**
     * Forces the view to be of a certain size
     */
    fun VIEW.setWidth(width: Float): VIEW

    /**
     * Forces the view to be of a certain size
     */
    fun VIEW.setHeight(height: Float): VIEW

    /**
     * Adds a margin around an item.
     */
    fun VIEW.margin(
            left: Float = 0f,
            top: Float = 0f,
            right: Float = 0f,
            bottom: Float = 0f
    ): VIEW

    fun VIEW.margin(
            horizontal: Float = 0f,
            top: Float = 0f,
            bottom: Float = 0f
    ) = this.margin(horizontal, top, horizontal, bottom)

    fun VIEW.margin(
            horizontal: Float = 0f,
            vertical: Float = 0f
    ) = this.margin(horizontal, vertical, horizontal, vertical)

    fun VIEW.margin(amount: Float) = this.margin(amount, amount, amount, amount)

    /**
     * Creates a blank space.
     */
    fun space(size: Point): VIEW

    /**
     * Gets the lifecycle of a view.
     */
    val VIEW.lifecycle: ObservableProperty<Boolean>

    /**
     * Gets the coroutine scope of a view.
     */
    val VIEW.scope: CoroutineScope
        get() = lifecycle.scope
}