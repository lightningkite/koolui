package com.lightningkite.koolui.image

expect class Displayable {
    companion object {
        fun fromSvgString(svg: String): Displayable
        val blank: Displayable
    }
}
