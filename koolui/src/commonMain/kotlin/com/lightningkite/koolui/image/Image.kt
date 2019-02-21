package com.lightningkite.koolui.image

expect class Image {
    companion object {
        fun fromSvgString(svg: String): Image
        val blank: Image
    }
}
