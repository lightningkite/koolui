package com.lightningkite.koolui.views

import com.lightningkite.koolui.concepts.TextSize

interface HasScale {
    val scale: Double

    val TextSize.javafx
        get() = when (this) {
            TextSize.Tiny -> 10.0 * scale
            TextSize.Body -> 14.0 * scale
            TextSize.Subheader -> 18.0 * scale
            TextSize.Header -> 24.0 * scale
        }
}