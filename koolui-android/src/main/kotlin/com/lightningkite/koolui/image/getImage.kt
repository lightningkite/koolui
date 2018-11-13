package com.lightningkite.koolui.image

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.lightningkite.koolui.resources.Resources

actual suspend fun Resources.getImage(filename: String): Displayable = if (filename.endsWith("svg")) {
    Resources.getString(filename).let { Displayable.fromSvgString(it) }
} else {
    Resources.getByteArray(filename).let {
        @Suppress("DEPRECATION")
        Displayable(BitmapDrawable(BitmapFactory.decodeByteArray(it, 0, it.size)))
    }
}
