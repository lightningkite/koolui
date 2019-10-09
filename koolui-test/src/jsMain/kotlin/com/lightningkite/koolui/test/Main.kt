package com.lightningkite.koolui.test

import com.lightningkite.koolui.color.*
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.views.root.contentRoot
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window


class LayoutFactory(
        val underlying: LayoutHtmlViewFactory = LayoutHtmlViewFactory(theme, theme.main)
) : MyViewFactory<Layout<*, HTMLElement>>, ViewFactory<Layout<*, HTMLElement>> by underlying

class Factory(
        colorSet: ColorSet = theme.main
) : MyViewFactory<HTMLElement>, ViewFactory<HTMLElement> by HtmlViewFactory(myTheme, colorSet)

fun main(args: Array<String>) {
    window.onload = {
        document.body!!.appendChild(
//                LayoutFactory().run {
//                    underlying.nativeViewAdapter(contentRoot(MainVG()))
//                }
            Factory().contentRoot(MainVG())
        )
    }
}
