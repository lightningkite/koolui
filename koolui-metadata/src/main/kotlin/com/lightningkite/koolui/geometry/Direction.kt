package com.lightningkite.koolui.geometry

import kotlin.math.PI

enum class Direction(val uiPositive: Boolean, val vertical: Boolean, val radians: Double) {
    Right(
        uiPositive = true,
        vertical = false,
        radians = 0.0
    ),
    Up(
        uiPositive = false,
        vertical = true,
        radians = PI * .5
    ),
    Left(
        uiPositive = false,
        vertical = false,
        radians = PI
    ),
    Down(
        uiPositive = true,
        vertical = true,
        radians = PI * 1.5
    )
}
