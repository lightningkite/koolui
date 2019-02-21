package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point

fun Image.withSizing(
    defaultSize: Point? = null,
    scaleType: ImageScaleType = ImageScaleType.Fill
): ImageWithSizing = ImageWithSizing(this, defaultSize, scaleType)
