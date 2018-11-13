package com.lightningkite.koolui.image

import com.lightningkite.koolui.image.Displayable
import com.lightningkite.koolui.image.ImageScaleType
import com.lightningkite.recktangle.Point

class Image(
    val displayable: Displayable,
    val defaultSize: Point? = null,
    val scaleType: ImageScaleType = ImageScaleType.Fill
) {
    companion object {
        val none = Image(Displayable.blank)
    }
}

