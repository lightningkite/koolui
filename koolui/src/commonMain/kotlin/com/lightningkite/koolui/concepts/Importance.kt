package com.lightningkite.koolui.concepts

enum class Importance {
    Low,
    Normal,
    High,
    Danger;

    companion object {
        val values = values().toList()
    }

    val more: Importance
        get() {
            return values.getOrNull(values.indexOf(this) + 1) ?: Importance.Danger
        }
    val less: Importance
        get() {
            return values.getOrNull(values.indexOf(this) - 1) ?: Importance.Low
        }
}