package com.lightningkite.koolui.canvas

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.views.toJavaFX
import com.lightningkite.recktangle.Point
import javafx.geometry.VPos
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment

class JavaFXCanvas(val javaFxCanvas: javafx.scene.canvas.Canvas, val scale: Double) : Canvas {

    val ctx = javaFxCanvas.graphicsContext2D

    override val size: Point = Point()
        get() {
            field.x = javaFxCanvas.width.toFloat()
            field.y = javaFxCanvas.height.toFloat()
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

    override fun close(){
        ctx.closePath()
    }

    override fun stroke(color: Color, width: Float) {
        ctx.stroke = color.toJavaFX()
        ctx.lineWidth = width * scale
        ctx.stroke()
    }

    override fun fill(color: Color) {
        ctx.fill = color.toJavaFX()
        ctx.fill()
    }

    override fun clearRect(left: Float, top: Float, right: Float, bottom: Float) {
        ctx.clearRect(left.toDouble(), top.toDouble(), (right - left).toDouble(), (bottom - top).toDouble())
    }

    override fun text(text: String, x: Float, align: Align, baselineY: Float, textSize: Float, color: Color) {
        ctx.textAlign = when(align){
            Align.Start -> TextAlignment.LEFT
            Align.Center -> TextAlignment.CENTER
            Align.End -> TextAlignment.RIGHT
            Align.Fill -> TextAlignment.JUSTIFY
        }
        ctx.textBaseline = VPos.BASELINE
        ctx.font = Font.font(textSize.toDouble())
        ctx.fill = color.toJavaFX()
        ctx.fillText(text, x.toDouble(), baselineY.toDouble())
    }

    override fun measureTextWidth(text: String, textSize: Float): Float {
        return Text(text).run {
            font = Font.font(textSize.toDouble())
            wrappingWidth = 0.0
            lineSpacing = 0.0
            layoutBounds.width.toFloat()
        }
    }

    override fun measureTextHeight(text: String, textSize: Float): Float {
        return Text(text).run {
            font = Font.font(textSize.toDouble())
            wrappingWidth = 0.0
            lineSpacing = 0.0
            layoutBounds.height.toFloat()
        }
    }

}