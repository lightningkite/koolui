package com.lightningkite.koolui

import com.lightningkite.koolui.geometry.Align

fun Align.toWeb() = when (this) {
    Align.Start -> "flex-start"
    Align.Center -> "center"
    Align.End -> "flex-end"
    Align.Fill -> "stretch"
}