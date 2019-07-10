package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import kotlinx.cinterop.useContents
import platform.UIKit.UIView
import platform.UIKit.intrinsicContentSize

class IntrinsicLayoutDimensions(val view: UIView, val startMargin: Float = 8f, val endMargin: Float = 8f) : LeafDimensionLayouts() {
    override fun measureX(output: Measurement) {
        output.startMargin = startMargin
        output.endMargin = endMargin
        output.size = view.intrinsicContentSize.useContents { width }.toFloat()
    }

    override fun measureY(xSize: Float, output: Measurement) {
        output.startMargin = startMargin
        output.endMargin = endMargin
        output.size = view.intrinsicContentSize.useContents { height }.toFloat()
    }
}
