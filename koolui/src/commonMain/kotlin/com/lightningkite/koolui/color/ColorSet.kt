package com.lightningkite.koolui.color

import com.lightningkite.koolui.concepts.Importance

data class ColorSet(
    val background: Color = Color.white,
    val backgroundDisabled: Color = background.copy(alpha = background.alpha / 2),
    val backgroundHighlighted: Color = background.highlight(.2f),
    val foreground: Color = Color.black.copy(alpha = .8f),
    val foregroundDisabled: Color = foreground.copy(alpha = foreground.alpha / 2),
    val foregroundHighlighted: Color = foreground.copy(alpha = 1f)
) {

    fun importance(value: Importance): Color = when (value) {
        Importance.Low -> foregroundDisabled
        Importance.Normal -> foreground
        Importance.High -> foregroundHighlighted
        Importance.Danger -> destructive.foreground
    }

    companion object {
        fun basedOnBack(color: Color) = ColorSet(
            background = color,
            foreground = if (color.average > .7f) Color.black.copy(alpha = .8f) else Color.white.copy(alpha = .8f)
        )
        fun basedOnBackBold(color: Color) = ColorSet(
            background = color,
            foreground = if (color.average > .7f) Color.black else Color.white,
            foregroundHighlighted = if (color.average > .7f) Color.black else Color.white
        )

        val destructive = basedOnBack(Color.red)
    }
}