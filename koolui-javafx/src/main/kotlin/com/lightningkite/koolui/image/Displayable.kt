package com.lightningkite.koolui.image

import com.lightningkite.recktangle.Point
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import java.io.ByteArrayInputStream

actual class Displayable(val get: (scale: Float, setSize: Point?) -> Image) {

    actual companion object {
        actual fun fromSvgString(svg: String): Displayable {
            return Displayable { scale, size ->
                if (size != null) SVGRenderer.render(svg, size) else SVGRenderer.render(svg, scale)
            }
        }

        actual val blank: Displayable = Displayable { _, _ -> WritableImage(1, 1) }
    }
}
