package com.lightningkite.koolui.views

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.update
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

external class TouchEvent : Event {
    val targetTouches: JsTouchList
    val changedTouches: JsTouchList
    val touches: JsTouchList
}

external class JsTouchList {
    fun identifiedTouch(): JsTouch
    fun item(index: Int): JsTouch
    val length: Int
}

external class JsTouch {
    val identifier: Int
    val clientX: Float
    val clientY: Float
    val screenX: Float
    val screenY: Float
    val pageX: Float
    val pageY: Float
    val target: Element
    val radiusX: Float
    val radiusY: Float
    val rotationAngle: Float
    val force: Float
}

operator fun JsTouchList.iterator(): Iterator<JsTouch> {
    return (0 until length).asSequence().map { item(it) }.iterator()
}

var HTMLElement.ontouchstart: ((TouchEvent) -> dynamic)?
    get() = (this.asDynamic()).ontouchstart as ((Event) -> dynamic)?
    set(value){
        (this.asDynamic()).ontouchstart = value
    }

var HTMLElement.ontouchend: ((TouchEvent) -> dynamic)?
    get() = (this.asDynamic()).ontouchend as ((Event) -> dynamic)?
    set(value){
        (this.asDynamic()).ontouchend = value
    }

var HTMLElement.ontouchcancel: ((TouchEvent) -> dynamic)?
    get() = (this.asDynamic()).ontouchcancel as ((Event) -> dynamic)?
    set(value){
        (this.asDynamic()).ontouchcancel = value
    }

var HTMLElement.ontouchmove: ((TouchEvent) -> dynamic)?
    get() = (this.asDynamic()).ontouchmove as ((Event) -> dynamic)?
    set(value){
        (this.asDynamic()).ontouchmove = value
    }

fun HTMLElement.onNewTouch(listener:(Touch)->Unit){
    val touches = HashMap<Int, Touch.Impl>()
    this.ontouchstart = { event ->
        for (it in event.changedTouches) {
            val entry = Touch.Impl()
            touches[it.identifier] = entry
            entry.position.value.x = it.clientX
            entry.position.value.y = it.clientY
            entry.position.update()
        }
    }
    this.ontouchmove = { event ->
        for (it in event.changedTouches) {
            val entry = touches[it.identifier] ?: continue
            entry.position.value.x = it.clientX
            entry.position.value.y = it.clientY
        }
    }
    val removeHandler = fun(event: TouchEvent) {
        for (it in event.changedTouches) {
            val entry = touches.remove(it.identifier) ?: continue
            entry.position.value.x = it.clientX
            entry.position.value.y = it.clientY
            entry.position.update()
            entry.onRelease.invokeAll()
        }
    }
    this.ontouchcancel = removeHandler
    this.ontouchend = removeHandler

    this.onmousedown = {
        val entry = Touch.Impl()
        touches[0] = entry
        entry.position.value.x = it.clientX.toFloat()
        entry.position.value.y = it.clientY.toFloat()
        entry.position.update()
    }
    this.onmousemove = label@{
        val entry = touches[0] ?: return@label null
        entry.position.value.x = it.clientX.toFloat()
        entry.position.value.y = it.clientY.toFloat()
        null
    }
    this.onmouseup = label@{
        val entry = touches.remove(0) ?: return@label null
        entry.position.value.x = it.clientX.toFloat()
        entry.position.value.y = it.clientY.toFloat()
        entry.position.update()
        entry.onRelease.invokeAll()
    }
}