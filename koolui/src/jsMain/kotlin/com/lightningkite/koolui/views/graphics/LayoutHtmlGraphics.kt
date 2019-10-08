package com.lightningkite.koolui.views.graphics

import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.canvas.HtmlCanvas
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.makeElement
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement

interface LayoutHtmlGraphics : ViewFactoryGraphics<Layout<*, HTMLElement>>, LayoutViewWrapper<HTMLElement> {
    override fun canvas(draw: ObservableProperty<Canvas.() -> Unit>): Layout<*, HTMLElement> {
        return intrinsicLayout(makeElement<HTMLCanvasElement>("canvas")) { layout ->
            val c = HtmlCanvas(this)
            layout.isAttached.bind(draw) {
                c.ctx.clearRect(0.0, 0.0, c.element.width.toDouble(), c.element.height.toDouble())
                it(c)
            }
        }
    }
}