package com.lightningkite.koolui.geometry

class LinearPlacement(
    val weight: Float = 0f,
    val align: Align = Align.Fill
) {
    companion object {
        val fillStart = LinearPlacement(1f, Align.Start)
        val fillCenter = LinearPlacement(1f, Align.Center)
        val fillEnd = LinearPlacement(1f, Align.End)
        val fillFill = LinearPlacement(1f, Align.Fill)
        val wrapStart = LinearPlacement(0f, Align.Start)
        val wrapCenter = LinearPlacement(0f, Align.Center)
        val wrapEnd = LinearPlacement(0f, Align.End)
        val wrapFill = LinearPlacement(0f, Align.Fill)
    }
}
