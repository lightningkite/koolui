package com.lightningkite.koolui.view.graphics

import android.view.View
import com.lightningkite.koolui.android.CanvasView
import com.lightningkite.koolui.canvas.AndroidCanvas
import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.view.HasActivityAccess
import com.lightningkite.koolui.views.graphics.ViewFactoryGraphics
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind

interface LayoutAndroidGraphics : ViewFactoryGraphics<Layout<*, View>>, LayoutViewWrapper<View>, HasActivityAccess {
    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): Layout<*, View> = intrinsicLayout(CanvasView(activityAccess.context)) { layout ->
        val c = AndroidCanvas()
        layout.isAttached.bind(draw) {
            this.render = { c.canvas = this; c.it() }
        }
    }
}