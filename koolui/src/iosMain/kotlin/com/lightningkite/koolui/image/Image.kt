package com.lightningkite.koolui.image

import platform.UIKit.UIImage

actual class Image(val image: UIImage?) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image {
            return Image(null)
        }

        actual val blank: Image = Image(null)
    }
}
