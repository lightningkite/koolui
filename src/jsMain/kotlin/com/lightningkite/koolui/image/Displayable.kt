package com.lightningkite.koolui.image

external fun encodeURI(string: String): String

actual class Displayable(val image: String) {

    actual companion object {
        actual fun fromSvgString(svg: String): Displayable = Displayable(
            "data:image/svg+xml;utf8,${svg.trim().replace("#", "%23").replace('\"', '\'').replace(
                "<",
                "%3C"
            ).replace(">", "%3E")}"
        )

        actual val blank: Displayable = Displayable("")
    }
}
