package com.lightningkite.koolui.image

import com.lightningkite.kommon.string.Uri
import com.lightningkite.recktangle.Point
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import java.io.ByteArrayInputStream

actual class Image(val get: (scale: Float, setSize: Point?) -> Image) {

    actual companion object {
        actual fun fromSvgString(svg: String): com.lightningkite.koolui.image.Image {
            return Image { scale, size ->
                if (size != null) SVGRenderer.render(svg, size) else SVGRenderer.render(svg, scale)
            }
        }

        actual fun fromByteArray(byteArray: ByteArray): com.lightningkite.koolui.image.Image {
            val backing = Image(ByteArrayInputStream(byteArray))
            return Image { _, _ -> backing }
        }

        actual val blank: com.lightningkite.koolui.image.Image = Image { _, _ -> WritableImage(1, 1) }

        actual suspend fun fromUrlUnsafe(url: Uri): com.lightningkite.koolui.image.Image = com.lightningkite.koolui.image.Image { _, _ ->
            Image(url.string)
        }
        actual suspend fun fromUrlUnsafe(url: String): com.lightningkite.koolui.image.Image = fromUrlUnsafe(Uri(url))
    }
}
