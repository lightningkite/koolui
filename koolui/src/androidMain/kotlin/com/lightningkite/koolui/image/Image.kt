package com.lightningkite.koolui.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import java.lang.Exception
import com.lightningkite.koolui.ApplicationAccess
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.lightningkite.kommon.string.Uri
import com.pixplicity.sharp.Sharp
import com.pixplicity.sharp.SharpDrawable

actual class Image(val drawable: Drawable) {

    actual companion object {
        private val SVGCache = HashMap<String, Drawable>()
        actual fun fromSvgString(svg: String): Image = Image(SVGCache.getOrPut(svg) {
            try {
                Sharp.loadString(svg).sharpPicture.let { pic ->
                    val drawable = object : SharpDrawable(pic.picture) {
                        override fun getIntrinsicHeight(): Int = (super.getIntrinsicHeight() * (ApplicationAccess.access?.context?.resources?.displayMetrics?.density ?: 2f)).toInt()
                        override fun getIntrinsicWidth(): Int = (super.getIntrinsicWidth() * (ApplicationAccess.access?.context?.resources?.displayMetrics?.density ?: 2f)).toInt()
                    }
                    drawable.setCaching(true)
                    drawable.setCacheScale(ApplicationAccess.access?.context?.resources?.displayMetrics?.density ?: 2f)
                    drawable
                }
            } catch (e: Exception) {
                Log.e("Image", "Failed to parse SVG: $svg")
                throw e
            }
        })

        actual fun fromByteArray(byteArray: ByteArray): Image {
            return Image(BitmapDrawable(
                    ApplicationAccess.access!!.context.resources,
                    android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            ))
        }

        actual val blank: Image = Image(ColorDrawable(0x0))

        actual suspend fun fromUrlUnsafe(url: Uri): Image = suspendCoroutine { continuation ->
            Picasso.get().load(url.string).into(object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    continuation.resumeWithException(e ?: Exception("Unknown issue"))
                }

                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                    continuation.resume(Image(BitmapDrawable(bitmap)))
                }
            })
        }

        actual suspend fun fromUrlUnsafe(url: String): Image = fromUrlUnsafe(Uri(url))
    }
}
