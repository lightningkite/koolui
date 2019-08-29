package com.lightningkite.koolui.views

import com.lightningkite.kommon.collection.pop
import com.lightningkite.kommon.collection.push
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme

interface Themed {
    val theme: Theme
    val colorSet: ColorSet
    fun <T> usingColorSet(colorSet: ColorSet, action: ()->T): T

    private class Impl(override val theme: Theme, override var colorSet: ColorSet) : Themed {
        val colorSetStack = ArrayList<ColorSet>()
        override fun <T> usingColorSet(colorSet: ColorSet, action: () -> T): T {
            colorSetStack.push(colorSet)
            this.colorSet = colorSet
            val result = action()
            colorSetStack.pop()
            this.colorSet = colorSetStack.lastOrNull() ?: theme.main
            return result
        }
    }
    companion object {
        fun impl(theme: Theme, colorSet: ColorSet = theme.main): Themed = Impl(theme, colorSet)
    }
}

