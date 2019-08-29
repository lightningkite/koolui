package com.lightningkite.koolui.image

import com.lightningkite.koolui.color.Color
import com.lightningkite.recktangle.Point

class ImageWithOptions(
        val image: Image,
        val defaultSize: Point? = null,
        val tint: Color = Color.white,
        val scaleType: ImageScaleType = ImageScaleType.Fill
) {
    companion object {
        val none = ImageWithOptions(Image.blank)
    }
}

