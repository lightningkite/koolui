package com.lightningkite.koolui.image

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.larvalabs.svgandroid.SVG
import com.larvalabs.svgandroid.SVGBuilder
import java.lang.Exception
import com.lightningkite.koolui.ApplicationAccess

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

        actual fun fromByteArray(byteArray: ByteArray): Image {
            return Image(BitmapDrawable(
                    ApplicationAccess.access!!.context.resources,
                    android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            ))
        }

        actual val blank: Image = Image(ColorDrawable(0x0))
    }
}
