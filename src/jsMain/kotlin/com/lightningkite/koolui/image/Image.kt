package com.lightningkite.koolui.image

external fun encodeURI(string: String): String

actual class Image(val image: String) {

    actual companion object {
        actual fun fromSvgString(svg: String): Image = Image(
            "data:image/svg+xml;utf8,${svg.trim().replace("#", "%23").replace('\"', '\'').replace(
                "<",
                "%3C"
            ).replace(">", "%3E")}"
        )

        actual val blank: Image = Image("")
    }
}
