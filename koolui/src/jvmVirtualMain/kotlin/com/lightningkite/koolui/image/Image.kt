package com.lightningkite.koolui.image

actual class Image(val data: ByteArray) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image {
            return Image(svg.toByteArray())
        }

        actual fun fromByteArray(byteArray: ByteArray): Image {
            return Image(byteArray)
        }

        actual val blank: Image = Image(byteArrayOf())
    }
}
