package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.BaseDimensionCalculator
import com.lightningkite.koolui.layout.Layout
import kotlinx.cinterop.useContents
import platform.UIKit.UIView
import platform.UIKit.intrinsicContentSize


class IntrinsicXDimensionCalculator(val view: UIView): BaseDimensionCalculator() {
    override fun measure(output: Measurement) {
        output.startMargin = 0f
        output.size = view.intrinsicContentSize.useContents { width }.toFloat()
        output.endMargin = 0f
    }

    override fun layoutChildren(size: Float) {}
}

class IntrinsicYDimensionCalculator(val view: UIView): BaseDimensionCalculator() {
    override fun measure(output: Measurement) {
        output.startMargin = 0f
        output.size = view.intrinsicContentSize.useContents { height }.toFloat()
        output.endMargin = 0f
    }

    override fun layoutChildren(size: Float) {}
}

fun <S: UIView> Layout.Companion.intrinsic(view: S) = Layout(view.adapter, IntrinsicXDimensionCalculator(view), IntrinsicYDimensionCalculator(view))
