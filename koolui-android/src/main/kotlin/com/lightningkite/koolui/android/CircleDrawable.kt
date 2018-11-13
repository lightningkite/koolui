package com.lightningkite.koolui.android

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class CircleDrawable(color: Int) : Drawable() {

    val paint = Paint().apply {
        this.style = Paint.Style.FILL
        this.color = color
    }

    override fun draw(canvas: Canvas) {
        val minDimen = Math.min(bounds.width(), bounds.height())
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), minDimen / 2f, paint)
    }

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

}
