package com.lightningkite.koolui.views

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.makeElement
import com.lightningkite.koolui.onResize
import com.lightningkite.koolui.views.basic.LayoutHtmlBasic
import com.lightningkite.koolui.views.graphics.LayoutHtmlGraphics
import com.lightningkite.koolui.views.interactive.LayoutHtmlInteractive
import com.lightningkite.koolui.views.layout.LayoutHtmlLayout
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.recktangle.Rectangle
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.events.Event
import kotlin.browser.document

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

    fun defaultCss() = HtmlViewFactory.DEFAULT_CSS_TEMPLATE
            .replace("!!mfn", theme.main.foreground.toWeb())
            .replace("!!mfh", theme.main.foregroundHighlighted.toWeb())
            .replace("!!mfd", theme.main.foregroundDisabled.toWeb())
            .replace("!!mbn", theme.main.background.toWeb())
            .replace("!!mbh", theme.main.backgroundHighlighted.toWeb())
            .replace("!!mbd", theme.main.backgroundDisabled.toWeb())
            .replace("!!bfn", theme.bar.foreground.toWeb())
            .replace("!!bfh", theme.bar.foregroundHighlighted.toWeb())
            .replace("!!bfd", theme.bar.foregroundDisabled.toWeb())
            .replace("!!bbn", theme.bar.background.toWeb())
            .replace("!!bbh", theme.bar.backgroundHighlighted.toWeb())
            .replace("!!bbd", theme.bar.backgroundDisabled.toWeb())
            .replace("!!afn", theme.accent.foreground.toWeb())
            .replace("!!afh", theme.accent.foregroundHighlighted.toWeb())
            .replace("!!afd", theme.accent.foregroundDisabled.toWeb())
            .replace("!!abn", theme.accent.background.toWeb())
            .replace("!!abh", theme.accent.backgroundHighlighted.toWeb())
            .replace("!!abd", theme.accent.backgroundDisabled.toWeb())
            .replace("!!dfn", ColorSet.destructive.foreground.toWeb())
            .replace("!!dfh", ColorSet.destructive.foregroundHighlighted.toWeb())
            .replace("!!dfd", ColorSet.destructive.foregroundDisabled.toWeb())
            .replace("!!dbn", ColorSet.destructive.background.toWeb())
            .replace("!!dbh", ColorSet.destructive.backgroundHighlighted.toWeb())
            .replace("!!dbd", ColorSet.destructive.backgroundDisabled.toWeb())

    fun applyDefaultCss() {
        val cssElement = document.createElement("style") as HTMLStyleElement
        cssElement.type = "text/css"
        cssElement.appendChild(document.createTextNode(defaultCss()))
        document.head!!.appendChild(cssElement)
    }

    override var root: Layout<*, HTMLElement>? = null

    override fun contentRoot(view: Layout<*, HTMLElement>): Layout<*, HTMLElement> {
        applyDefaultCss()
        return super<LayoutVFRootAndDialogs>.contentRoot(view)
    }

    override fun nativeViewAdapter(wraps: Layout<*, HTMLElement>): HTMLElement {
        val view = wraps.viewAsBase
        val rect = Rectangle()
        val onResize = {
            rect.right = view.clientWidth.toFloat()
            rect.bottom = view.clientHeight.toFloat()
            if(rect.right != 0f && rect.bottom != 0f) {
                wraps.layout(rect)
            }
        }
        view.onResize {
            kotlin.browser.window.setTimeout(onResize, 1)
        }
        onResize()
        return view
    }

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