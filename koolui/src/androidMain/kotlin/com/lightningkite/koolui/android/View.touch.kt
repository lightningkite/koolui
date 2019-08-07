package com.lightningkite.koolui.android

import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.lightningkite.koolui.views.Touch
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.update

fun View.onNewTouch(handler: (Touch) -> Unit) {
    val touches = SparseArray<Touch.Impl>()
    this.setOnTouchListener { v, event ->
        val identifier = event.getPointerId(event.actionIndex)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val entry = Touch.Impl()
                touches.put(identifier, entry)
                entry.position.value.x = event.x
                entry.position.value.y = event.y
                entry.position.update()
            }
            MotionEvent.ACTION_MOVE -> {
                val entry = touches[identifier] ?: return@setOnTouchListener true
                entry.position.value.x = event.x
                entry.position.value.y = event.y
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                val entry = touches[identifier] ?: return@setOnTouchListener true
                touches.removeAt(identifier)
                entry.position.value.x = event.x
                entry.position.value.y = event.y
                entry.position.update()
                entry.onRelease.invokeAll()
            }
        }
        true
    }
}