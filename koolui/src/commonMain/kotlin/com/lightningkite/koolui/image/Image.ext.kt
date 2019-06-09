package com.lightningkite.koolui.image

import com.lightningkite.kommon.exception.stackTraceString
import com.lightningkite.kommon.string.Uri
import com.lightningkite.recktangle.Point

fun Image.withSizing(
        defaultSize: Point? = null,
        scaleType: ImageScaleType = ImageScaleType.Fill
): ImageWithSizing = ImageWithSizing(this, defaultSize, scaleType)

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