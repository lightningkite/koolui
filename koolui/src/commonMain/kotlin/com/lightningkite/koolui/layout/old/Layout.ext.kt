package com.lightningkite.koolui.layout.old

import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.listen

fun <S : V, V> Layout.Companion.vertical(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.x }),
        y = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.y })
).apply { children.forEach { addChild(it.second) } }

fun <S : V, V> Layout.Companion.horizontal(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.y })
).apply { children.forEach { addChild(it.second) } }

fun <S : V, V> Layout.Companion.align(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<AlignPair, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.horizontal to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.vertical to it.second.y })
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
        x = FrameDimensionCalculator(leftMargin, rightMargin) { child.x },
        y = FrameDimensionCalculator(topMargin, bottomMargin) { child.y }
).apply { addChild(child) }

fun <S : V, V> Layout.Companion.swap(
        viewAdapter: ViewAdapter<S, V>,
        child: ObservableProperty<Layout<*, V>>
): Layout<S, V> {
    var currentChild: Layout<*, V> = child.value
    return Layout<S, V>(
            viewAdapter = viewAdapter,
            x = DeferDimensionCalculator { currentChild.x },
            y = DeferDimensionCalculator { currentChild.y }
    ).apply {
        addChild(currentChild)
        isAttached.bind(child) {
            if(child == currentChild) return@bind
            removeChild(currentChild)
            currentChild = it
            addChild(it)
            invalidate()
        }
    }
}

fun <S : V, V> Layout.Companion.swapStatic(
        viewAdapter: ViewAdapter<S, V>,
        child: ObservableProperty<Layout<*, V>>,
        sizingChild: Layout<*, V>
): Layout<S, V> {
    var currentChild: Layout<*, V> = child.value
    return Layout<S, V>(
            viewAdapter = viewAdapter,
            x = SeparateMeasureAndLayoutDimensionCalculator(
                    measure = { sizingChild.x },
                    layout = { currentChild.x }
            ) ,
            y = SeparateMeasureAndLayoutDimensionCalculator(
                    measure = { sizingChild.y },
                    layout = { currentChild.y }
            )
    ).apply {
        addChild(currentChild)
        isAttached.bind(child) {
            if(child == currentChild) return@bind
            removeChild(currentChild)
            currentChild = it
            addChild(it)
            invalidate()
        }
    }
}

fun <S : V, V> Layout.Companion.leaf(
        viewAdapter: ViewAdapter<S, V>,
        margin: Float,
        width: Float,
        height: Float
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = LeafDimensionCalculator(margin, width, margin),
        y = LeafDimensionCalculator(margin, height, margin)
)

fun Layout<*, *>.forceWidth(width: Float) {
    x = ForceSizeDimensionCalculator(x, width)
}

fun Layout<*, *>.forceHeight(height: Float) {
    y = ForceSizeDimensionCalculator(y, height)
}

fun Layout<*, *>.forceXMargins(left: Float, right: Float) {
    x = ForceMarginDimensionCalculator(x, left, right)
}

fun Layout<*, *>.forceYMargins(top: Float, bottom: Float) {
    y = ForceMarginDimensionCalculator(y, top, bottom)
}
