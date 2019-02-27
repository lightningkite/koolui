package com.lightningkite.koolui.test

import com.lightningkite.koolui.color.*
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.builders.*
import com.lightningkite.koolui.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window


class Factory() : MyViewFactory<HTMLElement>, ViewFactory<HTMLElement> by HtmlViewFactory(Theme.dark()) {}

fun main(args: Array<String>) {
    window.onload = {
        document.body!!.appendChild(
                Factory().contentRoot(MainVG<HTMLElement>())
        )
    }
}
