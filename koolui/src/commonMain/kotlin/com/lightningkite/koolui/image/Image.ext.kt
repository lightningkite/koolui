package com.lightningkite.koolui.image

import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.color.Color
import com.lightningkite.recktangle.Point

fun Image.withOptions(
        defaultSize: Point? = null,
        tint: Color = Color.white,
        scaleType: ImageScaleType = ImageScaleType.Fill
): ImageWithOptions = ImageWithOptions(this, defaultSize = defaultSize, tint = tint, scaleType = scaleType)

suspend fun Image.Companion.fromUrl(url: Uri): Image? = try {
    Image.Companion.fromUrlUnsafe(url)
} catch (e: Exception) {
    println(e.stackTraceString()); null
}

suspend fun Image.Companion.fromUrl(url: String): Image? = try {
    Image.Companion.fromUrlUnsafe(url)
} catch (e: Exception) {
    println(e.stackTraceString()); null
}