package com.lightningkite.koolui.image

import com.lightningkite.kommon.string.Uri

actual class Image(val data: ByteArray) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image {
            return Image(svg.toByteArray())
        }

        actual fun fromByteArray(byteArray: ByteArray): Image {
            return Image(byteArray)
        }

        actual val blank: Image = Image(byteArrayOf())

        actual suspend fun fromUrlUnsafe(url: Uri): Image = Image(url.string.toByteArray())
        actual suspend fun fromUrlUnsafe(url: String): Image = fromUrlUnsafe(Uri(url))
    }
}
