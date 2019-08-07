package com.lightningkite.koolui.canvas

import android.graphics.*
import android.graphics.Paint
import android.text.TextPaint
import com.lightningkite.koolui.android.dip
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.recktangle.Point

class AndroidCanvas(): Canvas {

    lateinit var canvas: android.graphics.Canvas

    val currentPath = Path()
    val currentPaint = Paint()

    override val size: Point = Point()
        get() {
            field.x = canvas.width.toFloat()
            field.y = canvas.height.toFloat()
            return field
        }

    override fun beginPath() {
        currentPath.reset()
    }

    override fun move(x: Float, y: Float) {
        currentPath.moveTo(x, y)
    }

    override fun line(x: Float, y: Float) {
        currentPath.lineTo(x, y)
    }

    override fun curve(controlX: Float, controlY: Float, x: Float, y: Float) {
        currentPath.quadTo(controlX, controlY, x, y)
    }

    override fun curve(control1X: Float, control1Y: Float, control2X: Float, control2Y: Float, x: Float, y: Float) {
        currentPath.cubicTo(control1X, control1Y, control2X, control2Y, x, y)
    }

    override fun close() {
        currentPath.close()
    }

    override fun stroke(color: Color, width: Float) {
        currentPaint.color = color.toInt()
        currentPaint.style = Paint.Style.STROKE
        currentPaint.strokeWidth = width * dip
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun fill(color: Color) {
        currentPaint.color = color.toInt()
        currentPaint.style = Paint.Style.FILL
        canvas.drawPath(currentPath, currentPaint)
    }

    val clearPaint = Paint().apply {
        color = 0x0
        style = Paint.Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    override fun clearRect(left: Float, top: Float, right: Float, bottom: Float) {
        canvas.drawRect(left, top, right, bottom, clearPaint)
    }

    val textPaint = TextPaint()
    override fun text(text: String, x: Float, align: Align, baselineY: Float, textSize: Float, color: Color) {
        textPaint.textSize = textSize * dip
        textPaint.color = color.toInt()
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = when(align){
            Align.Start -> Paint.Align.LEFT
            Align.Center -> Paint.Align.CENTER
            Align.End -> Paint.Align.RIGHT
            Align.Fill -> Paint.Align.CENTER
        }
        canvas.drawText(text, x, baselineY, textPaint)
    }

    override fun measureTextWidth(text: String, textSize: Float): Float {
        textPaint.textSize = textSize * dip
        return textPaint.measureText(text)
    }

    override fun measureTextHeight(text: String, textSize: Float): Float {
        textPaint.textSize = textSize * dip
        return textPaint.fontMetrics.bottom - textPaint.fontMetrics.top
    }
}