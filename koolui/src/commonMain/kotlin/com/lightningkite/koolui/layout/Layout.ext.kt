package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement

fun Layout.Companion.vertical(
        viewAdapter: ViewAdapter,
        children: List<Pair<LinearPlacement, Layout>>
) = Layout(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.x }),
        y = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.y })
)

fun Layout.Companion.horizontal(
        viewAdapter: ViewAdapter,
        children: List<Pair<LinearPlacement, Layout>>
) = Layout(
        viewAdapter = viewAdapter,
        x = LinearDimensionCalculator(children.asSequence().map { it.first.weight to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.align to it.second.y })
)

fun Layout.Companion.align(
        viewAdapter: ViewAdapter,
        children: List<Pair<AlignPair, Layout>>
) = Layout(
        viewAdapter = viewAdapter,
        x = AlignDimensionCalculator(children.asSequence().map { it.first.horizontal to it.second.x }),
        y = AlignDimensionCalculator(children.asSequence().map { it.first.vertical to it.second.y })
)

fun Layout.Companion.frame(
        viewAdapter: ViewAdapter,
        child: Layout,
        leftMargin: Float = 0f,
        rightMargin: Float = 0f,
        topMargin: Float = 0f,
        bottomMargin: Float = 0f
) = Layout(
        viewAdapter = viewAdapter,
        x = FrameDimensionCalculator(leftMargin, rightMargin) { child.x },
        y = FrameDimensionCalculator(topMargin, bottomMargin) { child.y }
)

fun Layout.Companion.leaf(
        viewAdapter: ViewAdapter,
        margin: Float,
        width: Float,
        height: Float
) = Layout(
        viewAdapter = viewAdapter,
        x = LeafDimensionCalculator(margin, width, margin),
        y = LeafDimensionCalculator(margin, height, margin)
)

fun Layout.forceWidth(width: Float) {
    x = ForceSizeDimensionCalculator(x, width)
}

fun Layout.forceHeight(height: Float) {
    y = ForceSizeDimensionCalculator(y, height)
}

fun Layout.forceXMargins(left: Float, right: Float) {
    x = ForceMarginDimensionCalculator(x, left, right)
}

fun Layout.forceYMargins(top: Float, bottom: Float) {
    y = ForceMarginDimensionCalculator(y, top, bottom)
}