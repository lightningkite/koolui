package com.lightningkite.koolui.canvas

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.recktangle.Point
import org.w3c.dom.*

class HtmlCanvas(val element: HTMLCanvasElement): Canvas {

    val ctx = element.getContext("2d") as CanvasRenderingContext2D

    override val size: Point = Point()
        get() {
            field.x = element.width.toFloat()
            field.y = element.height.toFloat()
            return field
        }

    override fun beginPath() {
        ctx.beginPath()
    }

    override fun move(x: Float, y: Float) {
        ctx.moveTo(x.toDouble(), y.toDouble())
    }

    override fun line(x: Float, y: Float) {
        ctx.lineTo(x.toDouble(), y.toDouble())
    }

    override fun curve(controlX: Float, controlY: Float, x: Float, y: Float) {
        ctx.quadraticCurveTo(controlX.toDouble(), controlY.toDouble(), x.toDouble(), y.toDouble())
    }

    override fun curve(control1X: Float, control1Y: Float, control2X: Float, control2Y: Float, x: Float, y: Float) {
        ctx.bezierCurveTo(control1X.toDouble(), control1Y.toDouble(), control2X.toDouble(), control2Y.toDouble(), x.toDouble(), y.toDouble())
    }

    override fun close() {
        ctx.closePath()
    }

    override fun stroke(color: Color, width: Float) {
        ctx.strokeStyle = color.toAlphalessWeb()
        ctx.lineWidth = width.toDouble()
        ctx.stroke()
    }

    override fun fill(color: Color) {
        ctx.fillStyle = color.toAlphalessWeb()
        ctx.fill()
    }

    override fun clearRect(left: Float, top: Float, right: Float, bottom: Float) {
        ctx.clearRect(left.toDouble(), top.toDouble(), (right - left).toDouble(), (bottom - top).toDouble())
    }

    override fun text(text: String, x: Float, align: Align, baselineY: Float, textSize: Float, color: Color) {
        ctx.font = "${textSize}px sans-serif"
        ctx.textAlign = when(align){
            Align.Start -> CanvasTextAlign.START
            Align.Center -> CanvasTextAlign.CENTER
            Align.End -> CanvasTextAlign.END
            Align.Fill -> CanvasTextAlign.CENTER
        }
        ctx.textBaseline = CanvasTextBaseline.BOTTOM
        ctx.fillStyle = color.toAlphalessWeb()
        ctx.fillText(text, x.toDouble(), baselineY.toDouble())
    }

    override fun measureTextWidth(text: String, textSize: Float): Float {
        ctx.font = "${textSize}px sans-serif"
        return ctx.measureText(text).width.toFloat()
    }

    override fun measureTextHeight(text: String, textSize: Float): Float {
        return textSize
    }

}