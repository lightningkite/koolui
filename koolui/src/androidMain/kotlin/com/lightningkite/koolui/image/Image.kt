package com.lightningkite.koolui.image

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.larvalabs.svgandroid.SVG
import com.larvalabs.svgandroid.SVGBuilder
import java.lang.Exception

actual class Image(val drawable: Drawable) {

    actual companion object {
        private val SVGCache = HashMap<String, SVG>()
        actual fun fromSvgString(svg: String): Image = Image(SVGCache.getOrPut(svg) {
            try {
                SVGBuilder().readFromString(svg).build()
            } catch (e: Exception) {
                Log.e("Image", "Failed to parse SVG: $svg")
                throw e
            }
        }.drawable)

        actual val blank: Image = Image(ColorDrawable(0x0))
    }
}
