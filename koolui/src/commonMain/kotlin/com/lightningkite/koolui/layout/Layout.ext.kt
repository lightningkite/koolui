package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement

fun <S: V, V> Layout.Companion.vertical(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.x }),
        y = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.y })
)

fun <S: V, V> Layout.Companion.horizontal(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<LinearPlacement, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.y })
)

fun <S: V, V> Layout.Companion.align(
        viewAdapter: ViewAdapter<S, V>,
        children: List<Pair<AlignPair, Layout<*, V>>>
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.horizontal to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.vertical to it.second.y })
)

fun <S: V, V> Layout.Companion.frame(
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
)

fun <S: V, V> Layout.Companion.swap(
        viewAdapter: ViewAdapter<S, V>,
        child: ()->Layout<*, V>,
        leftMargin: Float = 0f,
        rightMargin: Float = 0f,
        topMargin: Float = 0f,
        bottomMargin: Float = 0f
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = FrameDimensionCalculator(leftMargin, rightMargin) { child().x },
        y = FrameDimensionCalculator(topMargin, bottomMargin) { child().y }
)

fun <S: V, V> Layout.Companion.leaf(
        viewAdapter: ViewAdapter<S, V>,
        margin: Float,
        width: Float,
        height: Float
) = Layout<S, V>(
        viewAdapter = viewAdapter,
        x = LeafDimensionCalculator(margin, width, margin),
        y = LeafDimensionCalculator(margin, height, margin)
)

fun <S: V, V> Layout<S, V>.forceWidth(width: Float) {
    x = ForceSizeDimensionCalculator(x, width)
}

fun <S: V, V> Layout<S, V>.forceHeight(height: Float) {
    y = ForceSizeDimensionCalculator(y, height)
}

fun <S: V, V> Layout<S, V>.forceXMargins(left: Float, right: Float) {
    x = ForceMarginDimensionCalculator(x, left, right)
}

fun <S: V, V> Layout<S, V>.forceYMargins(top: Float, bottom: Float) {
    y = ForceMarginDimensionCalculator(y, top, bottom)
}
