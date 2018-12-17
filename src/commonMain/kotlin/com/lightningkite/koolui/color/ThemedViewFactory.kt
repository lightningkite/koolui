package com.lightningkite.koolui.color

interface ThemedViewFactory<out T : ThemedViewFactory<T>> {

    val theme: Theme
    val colorSet: ColorSet

    fun withColorSet(colorSet: ColorSet): T

}