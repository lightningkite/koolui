package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.*
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.recktangle.Rectangle
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.UIKit.*
import platform.darwin.object_getClassName

class UIViewAdapter<S: UIView>(override val view: S): ViewAdapter<S, UIView> {

    val holding = HashMap<String, Any?>()
    val onResize = ArrayList<(Rectangle) -> Unit>(0)
    val myFrame = Rectangle()

    override fun updatePlacementX(start: Float, end: Float) {
        val newRect = view.frame.useContents {
            CGRectMake(
                    x = start.toDouble(),
                    y = origin.y,
                    width = (end - start).toDouble(),
                    height = size.height
            )
        }
        myFrame.left = start
        myFrame.right = end
        view.setFrame(newRect)
        onResize.invokeAll(myFrame)
        view.setNeedsLayout()
        if (view is UILabel) {
            println("Label: ${view.text}")
        }
        println("updatePlacementX on ${object_getClassName(view)?.toKString()} $start - $end")
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
        myFrame.top = start
        myFrame.bottom = end
        view.setFrame(newRect)
        onResize.invokeAll(myFrame)
        view.setNeedsLayout()
        if (view is UILabel) {
            println("Label: ${view.text}")
        }
        println("updatePlacementY on ${object_getClassName(view)?.toKString()} $start - $end")
    }

    override fun onAddChild(layout: Layout<*, UIView>) {
        viewAsBase.addSubview(layout.viewAsBase)
    }

    override fun onRemoveChild(layout: Layout<*, UIView>) {
        layout.viewAsBase.removeFromSuperview()
    }
}

val <S: UIView> S.adapter get() = UIViewAdapter(this)
