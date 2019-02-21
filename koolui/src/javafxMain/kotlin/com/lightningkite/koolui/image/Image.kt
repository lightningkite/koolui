package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point
import javafx.scene.image.Image
import javafx.scene.image.WritableImage

actual class Image(val get: (scale: Float, setSize: Point?) -> Image) {

    actual companion object {
        actual fun fromSvgString(svg: String): com.lightningkite.koolui.image.Image {
            return Image { scale, size ->
                if (size != null) SVGRenderer.render(svg, size) else SVGRenderer.render(svg, scale)
            }
        }

        actual val blank: com.lightningkite.koolui.image.Image = Image { _, _ -> WritableImage(1, 1) }
    }
}
