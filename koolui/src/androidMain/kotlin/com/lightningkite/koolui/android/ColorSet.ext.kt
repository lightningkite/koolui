package com.lightningkite.koolui.android

import android.content.res.ColorStateList
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.color.ColorSet

fun ColorSet.androidForeground() = ColorStateList(
    arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_hovered),
        intArrayOf()
    ),
    intArrayOf(
        foregroundDisabled.toInt(),
        foregroundHighlighted.toInt(),
        foregroundHighlighted.toInt(),
        foregroundHighlighted.toInt(),
        foreground.toInt()
    )
)

fun ColorSet.androidForegroundOverlay() = ColorStateList(
    arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_hovered),
        intArrayOf()
    ),
    intArrayOf(
        foregroundDisabled.toInt(),
        foregroundHighlighted.toInt(),
        foregroundHighlighted.toInt(),
        foregroundHighlighted.toInt(),
        Color.transparent.toInt()
    )
)

fun ColorSet.androidBackground() = ColorStateList(
    arrayOf(
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_hovered),
        intArrayOf()
    ),
    intArrayOf(
        backgroundDisabled.toInt(),
        backgroundHighlighted.toInt(),
        backgroundHighlighted.toInt(),
        backgroundHighlighted.toInt(),
        background.toInt()
    )
)