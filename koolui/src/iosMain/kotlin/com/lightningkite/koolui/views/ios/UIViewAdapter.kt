package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.*
import com.lightningkite.reacktive.invokeAll
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.UIKit.UIScrollView
import platform.UIKit.UIView
import platform.UIKit.intrinsicContentSize

class UIViewAdapter<S: UIView>(override val view: S): ViewAdapter<S, UIView> {

    val holding = HashMap<String, Any?>()
    val onResize = ArrayList<(CGRect)->Unit>(0)

    override fun updatePlacementX(start: Float, end: Float) {
        view.frame.useContents {
            this.origin.x = (start).toDouble()
            this.size.width = (start + end).toDouble()
        }
        onResize.invokeAll(view.frame.useContents { this })
    }

    override fun updatePlacementY(start: Float, end: Float) {
        view.frame.useContents {
            this.origin.y = (start).toDouble()
            this.size.height = (start + end).toDouble()
        }
        onResize.invokeAll(view.frame.useContents { this })
    }
}

val <S: UIView> S.adapter get() = UIViewAdapter(this)
