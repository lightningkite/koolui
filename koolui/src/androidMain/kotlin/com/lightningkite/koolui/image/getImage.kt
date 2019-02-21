package com.lightningkite.koolui.image

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.lightningkite.koolui.resources.Resources

actual suspend fun Resources.getImage(filename: String): Image = if (filename.endsWith("svg")) {
    Resources.getString(filename).let { Image.fromSvgString(it) }
} else {
    Resources.getByteArray(filename).let {
        @Suppress("DEPRECATION")
        Image(BitmapDrawable(BitmapFactory.decodeByteArray(it, 0, it.size)))
    }
}
