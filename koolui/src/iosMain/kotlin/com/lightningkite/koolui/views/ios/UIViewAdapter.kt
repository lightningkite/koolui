package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.*
import com.lightningkite.reacktive.invokeAll
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.UIKit.*

class UIViewAdapter<S: UIView>(override val view: S): ViewAdapter<S, UIView> {

    val holding = HashMap<String, Any?>()
    val onResize = ArrayList<(CGRect)->Unit>(0)

    override fun updatePlacementX(start: Float, end: Float) {
        val newRect = view.frame.useContents {
            CGRectMake(
                    x = start.toDouble(),
                    y = origin.y,
                    width = (end - start).toDouble(),
                    height = size.height
            )
        }
        view.setFrame(newRect)
        onResize.invokeAll(view.frame.useContents { this })
        view.setNeedsLayout()
    }

    override fun updatePlacementY(start: Float, end: Float) {
        val newRect = view.frame.useContents {
            CGRectMake(
                    x = origin.x,
                    y = start.toDouble(),
                    width = size.width,
                    height = (end - start).toDouble()
            )
        }
        view.setFrame(newRect)
        onResize.invokeAll(view.frame.useContents { this })
        view.setNeedsLayout()
    }

    override fun onAddChild(layout: Layout<*, UIView>) {
        viewAsBase.addSubview(layout.viewAsBase)
    }

    override fun onRemoveChild(layout: Layout<*, UIView>) {
        layout.viewAsBase.removeFromSuperview()
    }
}

val <S: UIView> S.adapter get() = UIViewAdapter(this)
