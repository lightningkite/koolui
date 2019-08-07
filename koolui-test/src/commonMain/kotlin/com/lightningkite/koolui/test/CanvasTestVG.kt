package com.lightningkite.koolui.test

import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.color.Color
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.recktangle.Point

class CanvasTestVG<VIEW> : MyViewGenerator<VIEW> {
    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = dependency.canvas(ConstantObservableProperty(fun Canvas.() {
        val w = size.x / 100
        val h = size.y / 100

        beginPath()
        move(0 * w, 0 * h)
        line(20 * w, 20 * h)
        move(20 * w, 0 * h)
        line(0 * w, 20 * h)
        stroke(Color.green, 1f)

        beginPath()
        move(40 * w, 40 * h)
        line(60 * w, 40 * h)
        line(60 * w, 60 * h)
        line(40 * w, 60 * h)
        close()
        fill(Color.red)
    }))

}