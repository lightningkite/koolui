package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.push
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.ExternalAccess
import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.lastOrNullObservableWithAnimations
import com.lightningkite.koolui.geometry.Align
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.image.withOptions
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.reacktive.list.StandardObservableList
import com.lightningkite.reacktive.list.asObservableList
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.lifecycle.listen
import com.lightningkite.reacktive.property.transform
import kotlinx.io.core.toByteArray

class PentagameTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Pentagame Test"

    val stack = StandardObservableList<MyViewGenerator<VIEW>>()

    init {
        stack.push(FrameVG())
    }

    val frameNumber = StandardObservableProperty(0)

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        frame(horizontal {
            +vertical {
                -horizontal {
                    -text("Here we go!")
                    +list(
                            data = listOf(1, 2, 3, 4, 5).asObservableList(),
                            direction = Direction.Right,
                            makeView = { itemObs, index ->
                                entryContext(
                                        label = "Entry",
                                        field = text(itemObs.transform { it.toString() })
                                )
                            }
                    )//.setHeight(100f)
                }
                +horizontal {
                    -list(
                            data = listOf(1, 2, 3, 4, 5).asObservableList(),
                            makeView = { itemObs, index ->
                                entryContext(
                                        label = "Label",
                                        field = imageButton(
                                                imageWithOptions = ConstantObservableProperty(MaterialIcon.accessAlarm.color(Color.white).withOptions()),
                                                label = itemObs.transform { it.toString() },
                                                importance = Importance.Normal,
                                                onClick = {
                                                    stack.push(OriginalTestVG())
                                                }
                                        )
                                )
                            }
                    )//.setWidth(100f)
                    +window(dependency, stack, listOf())
                }
            }
            +canvas(frameNumber.transform {
                fun Canvas.() {
                    val w = size.x / 100
                    val h = size.y / 100

                    val xPos = (it % 120) - 20
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
        })
    }
}

