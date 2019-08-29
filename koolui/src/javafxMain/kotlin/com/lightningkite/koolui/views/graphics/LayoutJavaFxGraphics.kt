package com.lightningkite.koolui.views.graphics

import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.wrap
import com.lightningkite.koolui.views.HasScale
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import javafx.scene.Node

interface LayoutJavaFxGraphics : ViewFactoryGraphics<Layout<*, Node>>, HasScale, LayoutViewWrapper<Node> {
    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): Layout<*, Node> {
        return wrap(javafx.scene.canvas.Canvas()) { lifecycle ->
            val c = JavaFXCanvas(this, scale)
            lifecycle.bind(draw){
                it(c)
            }
        }
    }
}