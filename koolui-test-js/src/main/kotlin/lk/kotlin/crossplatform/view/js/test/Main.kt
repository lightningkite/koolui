package lk.kotlin.crossplatform.view.js.test

import com.lightningkite.koolui.views.HtmlViewFactory
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.test.MainVG
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>){
    window.onload = {
        with(HtmlViewFactory(Theme.dark())){
            applyDefaultCss()
            val view = rootContainer(MainVG<HTMLElement>().generate(this))
            view.lifecycle.alwaysOn = true
            document.body!!.appendChild(view)
        }
    }
}
