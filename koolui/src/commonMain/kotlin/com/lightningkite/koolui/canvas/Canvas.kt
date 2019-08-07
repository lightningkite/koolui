package com.lightningkite.koolui.canvas

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.recktangle.Matrix2D
import com.lightningkite.recktangle.Point


interface Canvas {
    val size: Point

    fun beginPath()
    fun move(x: Float, y: Float)
    fun line(x: Float, y: Float)
    fun curve(controlX: Float, controlY: Float, x: Float, y:Float)
    fun curve(control1X: Float, control1Y: Float, control2X: Float, control2Y: Float, x: Float, y:Float)
    fun close()

    fun stroke(color: Color, width: Float)
    fun fill(color: Color)

    fun clearRect(left: Float, top: Float, right: Float, bottom: Float)

    fun text(text: String, x: Float, align: Align, baselineY: Float, textSize: Float, color: Color)
    fun measureTextWidth(text: String, textSize: Float): Float
    fun measureTextHeight(text: String, textSize: Float): Float
}

