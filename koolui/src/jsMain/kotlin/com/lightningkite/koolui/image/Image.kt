package com.lightningkite.koolui.image

import com.lightningkite.kommon.asInt8Array
import com.lightningkite.kommon.string.Uri
import org.w3c.dom.url.URL
import org.w3c.files.Blob

external fun encodeURI(string: String): String

actual class Image(val url: String? = null, val data: ByteArray? = null) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image = Image(
            url = "data:image/svg+xml;utf8,${svg.trim().replace("#", "%23").replace('\"', '\'').replace(
                "<",
                "%3C"
            ).replace(">", "%3E")}"
        )

        actual fun fromByteArray(byteArray: ByteArray): Image {
            return Image(data = byteArray)
        }

        actual val blank: Image = Image("")

        actual suspend fun fromUrlUnsafe(url: Uri): Image = Image(url.string)
        actual suspend fun fromUrlUnsafe(url: String): Image = Image(url)
    }
}
