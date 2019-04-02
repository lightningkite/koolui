package com.lightningkite.koolui.geometry

data class Measurement(var startMargin: Float = 8f, var size: Float = 0f, var endMargin: Float = 8f) {
    val totalSpace: Float get() = startMargin + size + endMargin
    fun set(other: Measurement) {
        this.startMargin = other.startMargin
        this.size = other.size
        this.endMargin = other.endMargin
    }
}