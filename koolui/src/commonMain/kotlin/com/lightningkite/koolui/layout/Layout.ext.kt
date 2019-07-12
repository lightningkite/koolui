package com.lightningkite.koolui.layout

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <S : V, V> Layout.Companion.align(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<AlignPair, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionLayout(children.map { it.first.horizontal to it.second.x }),
        y = AlignDimensionLayout(children.map { it.first.vertical to it.second.y })
).apply { children.forEach { addChild(it.second) } }

fun <S : V, V> Layout.Companion.vertical(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionLayout(children.map { it.first.align to it.second.x }),
        y = LinearDimensionLayout(children.map { it.first.weight to it.second.y })
).apply { children.forEach { addChild(it.second) } }

fun <S : V, V> Layout.Companion.horizontal(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = LinearDimensionLayout(children.map { it.first.weight to it.second.x }),
        y = AlignDimensionLayout(children.map { it.first.align to it.second.y })
).apply { children.forEach { addChild(it.second) } }

fun <S : V, V> Layout.Companion.frame(
        viewAdapter: ViewAdapter<S, V>,
        child: Layout<*, V>,
        leftMargin: Float = 0f,
        rightMargin: Float = 0f,
        topMargin: Float = 0f,
        bottomMargin: Float = 0f
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = FrameDimensionLayout(child.x, leftMargin, rightMargin),
        y = FrameDimensionLayout(child.y, topMargin, bottomMargin)
).apply { addChild(child) }

inline fun <S : V, V> Layout.Companion.swap(
        viewAdapter: ViewAdapter<S, V>,
        child: ObservableProperty<Pair<Layout<*, V>, Animation>>,
        crossinline applyExitTransition: (V, Animation, onComplete: () -> Unit) -> Unit = { _, _, complete -> complete() },
        crossinline applyEntranceTransition: (V, Animation) -> Unit = { _, _ -> }
): Layout<S, V> {
    var currentChild: Layout<*, V>? = null
    val x = SwapDimensionLayout(child.value.first.x)
    val y = SwapDimensionLayout(child.value.first.y)
    return Layout<S, V>(
            viewAdapter = viewAdapter,
            x = x,
            y = y
    ).apply {
        isAttached.bind(child) {
            val newLayout = it.first
            if (newLayout == currentChild) return@bind

            ApplicationAccess.post {
                val old = currentChild
                currentChild = null
                if (old != null) {
                    applyExitTransition(old.viewAsBase, it.second) {
                        if (old != currentChild) {
                            removeChild(old)
                        }
                    }

                    currentChild = newLayout
                    newLayout.parent?.removeChild(newLayout)
                    addChild(newLayout)
                    x.child = newLayout.x
                    y.child = newLayout.y
                    applyEntranceTransition(newLayout.viewAsBase, it.second)
                } else {
                    currentChild = newLayout
                    addChild(newLayout)
                    x.child = newLayout.x
                    y.child = newLayout.y
                }
            }
        }
    }
}

inline fun <S : V, V> Layout.Companion.swapStatic(
        viewAdapter: ViewAdapter<S, V>,
        child: ObservableProperty<Pair<Layout<*, V>, Animation>>,
        sizingChild: Layout<*, V>,
        crossinline applyExitTransition: (V, Animation, onComplete: () -> Unit) -> Unit = { _, _, complete -> complete() },
        crossinline applyEntranceTransition: (V, Animation) -> Unit = { _, _ -> }
): Layout<S, V> {
    var currentChild: Layout<*, V>? = null
    val x = SwapDimensionLayout(child.value.first.x)
    x.measurementChild = sizingChild.x
    val y = SwapDimensionLayout(child.value.first.y)
    y.measurementChild = sizingChild.y
    return Layout<S, V>(
            viewAdapter = viewAdapter,
            x = x,
            y = y
    ).apply {
        isAttached.bind(child) {
            val newLayout = it.first
            if (newLayout == currentChild) return@bind

            ApplicationAccess.post {
                val old = currentChild
                currentChild = null
                if (old != null) {
                    applyExitTransition(old.viewAsBase, it.second) {
                        if (old != currentChild) {
                            removeChild(old)
                        }
                    }

                    currentChild = newLayout
                    newLayout.parent?.removeChild(newLayout)
                    addChild(newLayout)
                    x.layoutChild = newLayout.x
                    y.layoutChild = newLayout.y
                    applyEntranceTransition(newLayout.viewAsBase, it.second)
                } else {
                    currentChild = newLayout
                    newLayout.parent?.removeChild(newLayout)
                    addChild(newLayout)
                    x.layoutChild = newLayout.x
                    y.layoutChild = newLayout.y
                }
            }
        }
    }
}

fun Layout<*, *>.forceWidth(width: Float) {
    x = ForceSizeDimensionLayout(x, width)
}

fun Layout<*, *>.forceHeight(height: Float) {
    y = ForceSizeDimensionLayout(y, height)
}

fun Layout<*, *>.forceXMargins(left: Float, right: Float) {
    x = ForceMarginsDimensionLayout(x, left, right)
}

fun Layout<*, *>.forceYMargins(top: Float, bottom: Float) {
    y = ForceMarginsDimensionLayout(y, top, bottom)
}
