package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point

fun Displayable.asImage(
    defaultSize: Point? = null,
    scaleType: ImageScaleType = ImageScaleType.Fill
): Image = Image(this, defaultSize, scaleType)
