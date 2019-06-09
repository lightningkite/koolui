package com.lightningkite.koolui.color

import com.lightningkite.koolui.concepts.Importance

data class Theme(
    val main: ColorSet = ColorSet(),
    val bar: ColorSet = ColorSet.basedOnBackBold(Color.fromInt(0xFF5370CE.toInt())),
    val accent: ColorSet = ColorSet.basedOnBackBold(Color.fromInt(0xFFD82222.toInt()))
) {

    fun importance(value: Importance): ColorSet = when (value) {
        Importance.Low -> main
        Importance.Normal -> bar
        Importance.High -> accent
        Importance.Danger -> ColorSet.destructive
    }

    companion object {
        fun light(
            primaryColor: Color = Color.fromInt(0xFF5370CE.toInt()),
            secondaryColor: Color = Color.fromInt(0xFFD82222.toInt())
        ) = Theme(
            main = ColorSet.basedOnBack(Color.white),
            bar = ColorSet.basedOnBackBold(primaryColor),
            accent = ColorSet.basedOnBackBold(secondaryColor)
        )

        fun dark(
            primaryColor: Color = Color.fromInt(0xFF5370CE.toInt()),
            secondaryColor: Color = Color.fromInt(0xFFD82222.toInt())
        ) = Theme(
            main = ColorSet.basedOnBack(Color.gray(.1f)),
            bar = ColorSet.basedOnBackBold(primaryColor),
            accent = ColorSet.basedOnBackBold(secondaryColor)
        )
    }

}