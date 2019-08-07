package com.lightningkite.koolui.views

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.update
import javafx.scene.Node

fun Node.setOnNewTouch(onNewTouch: (Touch)->Unit) {
    val touches = HashMap<Int, Touch.Impl>()
    setOnTouchPressed { event ->
        val entry = Touch.Impl()
        touches[event.touchPoint.id] = entry
        entry.position.value.x = event.touchPoint.x.toFloat()
        entry.position.value.y = event.touchPoint.y.toFloat()
        entry.position.update()
    }
    setOnTouchMoved { event ->
        val entry = touches[event.touchPoint.id] ?: return@setOnTouchMoved
        entry.position.value.x = event.touchPoint.x.toFloat()
        entry.position.value.y = event.touchPoint.y.toFloat()
    }
    this.setOnTouchReleased { event ->
        val entry = touches.remove(event.touchPoint.id) ?: return@setOnTouchReleased
        entry.position.value.x = event.touchPoint.x.toFloat()
        entry.position.value.y = event.touchPoint.y.toFloat()
        entry.position.update()
        entry.onRelease.invokeAll()
    }

    this.setOnMousePressed { event ->
        val entry = Touch.Impl()
        touches[0] = entry
        entry.position.value.x = event.x.toFloat()
        entry.position.value.y = event.y.toFloat()
        entry.position.update()
    }
    this.setOnMouseMoved { event ->
        val entry = touches[0] ?: return@setOnMouseMoved
        entry.position.value.x = event.x.toFloat()
        entry.position.value.y = event.y.toFloat()
    }
    this.setOnMouseReleased { event ->
        val entry = touches.remove(0) ?: return@setOnMouseReleased
        entry.position.value.x = event.x.toFloat()
        entry.position.value.y = event.y.toFloat()
        entry.position.update()
        entry.onRelease.invokeAll()
    }
}