package com.lightningkite.koolui.image

import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.toNSData
import platform.UIKit.UIImage

actual class Image(val image: UIImage?) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image {
            return Image(null)
        }

        actual val blank: Image = Image(null)

        actual fun fromByteArray(byteArray: ByteArray): Image = Image(UIImage.imageWithData(byteArray.toNSData()))

        actual suspend fun fromUrlUnsafe(url: Uri): Image = fromUrlUnsafe(url.string)

        actual suspend fun fromUrlUnsafe(url: String): Image {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
