package com.lightningkite.koolui.image

import com.lightningkite.kommon.string.Uri

expect class Image {
    companion object {
        fun fromSvgString(svg: String): Image
        fun fromByteArray(byteArray: ByteArray): Image
        suspend fun fromUrlUnsafe(url: Uri): Image
        suspend fun fromUrlUnsafe(url: String): Image
        val blank: Image
    }
}
