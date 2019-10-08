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

    class SizedCanvas: javafx.scene.canvas.Canvas() {
        var onResize: ()->Unit = {}
        override fun isResizable(): Boolean = true
        override fun resize(width: Double, height: Double) {
            super.setWidth(width)
            super.setHeight(height)
            onResize()
        }
    }

    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): Layout<*, Node> {
        return wrap(SizedCanvas()) { lifecycle ->
            val c = JavaFXCanvas(this, scale)
            fun redraw(drawer: Canvas.()->Unit){
                c.ctx.clearRect(0.0, 0.0, c.javaFxCanvas.width, c.javaFxCanvas.height)
                drawer(c)
            }
            onResize = { redraw(draw.value) }
            lifecycle.bind(draw){
                redraw(it)
            }
        }
    }
}