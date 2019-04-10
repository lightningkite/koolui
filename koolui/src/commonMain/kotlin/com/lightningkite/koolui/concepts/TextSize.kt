package com.lightningkite.koolui.concepts

enum class TextSize {
    Tiny,
    Body,
    Subheader,
    Header;

    companion object {
        val values = values().toList()
    }

    val bigger: TextSize
        get() {
            return values.getOrNull(values.indexOf(this) + 1) ?: TextSize.Header
        }
    val smaller: TextSize
        get() {
            return values.getOrNull(values.indexOf(this) - 1) ?: TextSize.Tiny
        }
}