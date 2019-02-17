package com.lightningkite.koolui.image

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.larvalabs.svgandroid.SVG
import com.larvalabs.svgandroid.SVGBuilder

actual class Image(val drawable: Drawable) {

    actual companion object {
        private val SVGCache = HashMap<String, SVG>()
        actual fun fromSvgString(svg: String): Image = Image(SVGCache.getOrPut(svg) {
            SVGBuilder().readFromString(svg).build()
        }.drawable)

        actual val blank: Image = Image(ColorDrawable(0x0))
    }
}
