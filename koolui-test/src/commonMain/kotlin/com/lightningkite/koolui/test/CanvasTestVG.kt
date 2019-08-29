package com.lightningkite.koolui.test

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point

class CanvasTestVG<VIEW> : MyViewGenerator<VIEW> {
    val frameNumber = StandardObservableProperty(0)
    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        canvas(frameNumber.transform {
            fun Canvas.() {
                val w = size.x / 100
                val h = size.y / 100

                val xPos = (it % 120)-20
                beginPath()
                move((xPos + 0) * w, 0 * h)
                line((xPos + 20) * w, 20 * h)
                move((xPos + 20) * w, 0 * h)
                line((xPos + 0) * w, 20 * h)
                stroke(Color.green, 1f)

                beginPath()
                move(40 * w, 40 * h)
                line(60 * w, 40 * h)
                line(60 * w, 60 * h)
                line(40 * w, 60 * h)
                close()
                fill(Color.red)

                text("Frame: ${it}", 50 * w, Align.Center, 90 * h, h * 5, Color.white)
            }
        }).background(Color.gray).apply {
            lifecycle.listen(ApplicationAccess.onAnimationFrame) {
                frameNumber.value++
            }
        }

    }
}