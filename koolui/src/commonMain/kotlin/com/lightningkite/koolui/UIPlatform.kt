package com.lightningkite.koolui

enum class UIPlatform {
    Android,
    Javascript,
    IOS,
    JavaFX,
    Virtual;

    companion object
}

expect val UIPlatform.Companion.current: UIPlatform