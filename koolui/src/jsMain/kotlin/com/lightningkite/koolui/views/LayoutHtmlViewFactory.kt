package com.lightningkite.koolui.views

import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.makeElement
import com.lightningkite.koolui.views.basic.LayoutHtmlBasic
import com.lightningkite.koolui.views.graphics.LayoutHtmlGraphics
import com.lightningkite.koolui.views.interactive.LayoutHtmlInteractive
import com.lightningkite.koolui.views.layout.LayoutHtmlLayout
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import org.w3c.dom.HTMLElement

class LayoutHtmlViewFactory(
        override val theme: Theme,
        override val colorSet: ColorSet = theme.main
) : ViewFactory<Layout<*, HTMLElement>>,
        Themed by Themed.impl(theme, colorSet),
        LayoutHtmlBasic,
        LayoutHtmlInteractive,
        LayoutHtmlGraphics,
        LayoutHtmlLayout,
        ViewFactoryNavigationDefault<Layout<*, HTMLElement>>,
        LayoutVFRootAndDialogs<HTMLElement> {

    override var root: Layout<*, HTMLElement>? = null

    override fun nativeViewAdapter(wraps: Layout<*, HTMLElement>): HTMLElement = throw NotImplementedError()

    override fun defaultViewContainer(): HTMLElement = makeElement("div")

    class HTMLElementAdapter<SPECIFIC: HTMLElement>(override val view: SPECIFIC) : ViewAdapter<SPECIFIC, HTMLElement> {
        override fun updatePlacementX(start: Float, end: Float) {
            view.style.position = "absolute"
            view.style.left = start.toString() + "px"
            view.style.width = (end-start).toString() + "px"
        }

        override fun updatePlacementY(start: Float, end: Float) {
            view.style.position = "absolute"
            view.style.top = start.toString() + "px"
            view.style.height = (end-start).toString() + "px"
        }

        override fun onAddChild(layout: Layout<*, HTMLElement>) {
            view.appendChild(layout.viewAsBase)
        }

        override fun onRemoveChild(layout: Layout<*, HTMLElement>) {
            view.removeChild(layout.viewAsBase)
        }
    }
    override fun <SPECIFIC : HTMLElement> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, HTMLElement> = HTMLElementAdapter(this)

    class IntrinsicDimensionLayouts(val view: HTMLElement): LeafDimensionLayouts() {
        override fun measureX(output: Measurement) {
            output.startMargin = 8f
            output.endMargin = 8f
            output.size = view.scrollWidth.toFloat()
        }

        override fun measureY(xSize: Float, output: Measurement) {
            output.startMargin = 8f
            output.endMargin = 8f
            output.size = view.scrollHeight.toFloat()
        }

    }
    override fun intrinsicDimensionLayouts(view: HTMLElement): LeafDimensionLayouts = IntrinsicDimensionLayouts(view)

    override fun applyEntranceTransition(view: HTMLElement, animation: Animation) {

    }

    override fun applyExitTransition(view: HTMLElement, animation: Animation, onComplete: () -> Unit) {
        onComplete()
    }

}