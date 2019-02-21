package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point

class ImageWithSizing(
        val image: Image,
        val defaultSize: Point? = null,
        val scaleType: ImageScaleType = ImageScaleType.Fill
) {
    companion object {
        val none = ImageWithSizing(Image.blank)
    }
}

