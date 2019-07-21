package com.lightningkite.koolui

enum class UIPlatform {
    Android,
    Javascript,
    IOS,
    JavaFX,
    Lanterna,
    Virtual;

    companion object
}

expect val UIPlatform.Companion.current: UIPlatform