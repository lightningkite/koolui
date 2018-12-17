package com.lightningkite.koolui.image

actual class Displayable(val data: ByteArray) {

    actual companion object {
        actual fun fromSvgString(svg: String): Displayable {
            return Displayable(svg.toByteArray())
        }

        actual val blank: Displayable = Displayable(byteArrayOf())
    }
}
