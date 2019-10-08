package com.lightningkite.koolui

import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import kotlin.browser.document


fun Element.onResize(action: ()->Unit) {
    val zIndex = 0

    fun hiddenElement(): HTMLDivElement {
        val e = document.createElement("div") as HTMLDivElement
        e.style.position = "absolute"
        e.style.left = "0px"
        e.style.top = "0px"
        e.style.right = "0px"
        e.style.bottom = "0px"
        e.style.overflowWrap = "hidden"
        e.style.zIndex = zIndex.toString()
        e.style.visibility = "hidden"
        return e
    }
    val expand = hiddenElement()

    val expandChild = document.createElement("div") as HTMLDivElement
    expandChild.style.position = "absolute"
    expandChild.style.left = "0px"
    expandChild.style.top = "0px"
    expandChild.style.width = "10000000px"
    expandChild.style.height = "10000000px"
    expand.appendChild(expandChild)

    val shrink = hiddenElement()

    val shrinkChild = document.createElement("div") as HTMLDivElement
    shrinkChild.style.position = "absolute"
    shrinkChild.style.left = "0px"
    shrinkChild.style.top = "0px"
    shrinkChild.style.width = "200%"
    shrinkChild.style.height = "200%"
    shrink.appendChild(shrinkChild)

    this.appendChild(expand)
    this.appendChild(shrink)

    val size = this.getBoundingClientRect()
    var width = size.width
    var height = size.height
    fun setScroll(){
        expand.scrollLeft = 10000000.0
        expand.scrollTop = 10000000.0
        shrink.scrollLeft = 10000000.0
        shrink.scrollTop = 10000000.0
    }
    setScroll()

    val onScroll = { event: Event ->
        val size = this.getBoundingClientRect()
        val newWidth = size.width
        val newHeight = size.height
        if(newWidth != width || newHeight != height){
            width = newWidth
            height = newHeight
            action()
        }
        setScroll()
    }
    expand.addEventListener("scroll", onScroll)
    shrink.addEventListener("scroll", onScroll)
}