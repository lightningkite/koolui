package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.layout.Layout
import com.lightningkite.recktangle.Rectangle
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.Foundation.NSCoder
import platform.UIKit.UIView
import platform.UIKit.addSubview
import platform.darwin.sel_registerName

class LayoutRootView : UIView {
    @OverrideInit
    constructor(frame: CValue<CGRect>) : super(frame)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    lateinit var layout: Layout<*, UIView>

    fun setup(layout: Layout<*, UIView>) {
        this.layout = layout
        layout.isAttached.alwaysOn = true

        addSubview(layout.viewAsBase)
    }

    val rect = Rectangle()

    @ObjCAction
    fun layoutSubviews() {
        frame.useContents {
            rect.left = (origin.x).toFloat()
            rect.top = (origin.y).toFloat()
            rect.right = (origin.x + size.width).toFloat()
            rect.bottom = (origin.y + size.height).toFloat()
        }
        println("Laying out subviews in rect $rect")
        layout.layout(rect)
    }

    init {
        sel_registerName("layoutSubviews")
    }
}
