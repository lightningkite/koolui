package com.lightningkite.koolui.image

expect class Image {
    companion object {
        fun fromSvgString(svg: String): Image
        fun fromByteArray(byteArray: ByteArray): Image
        val blank: Image
    }
}
