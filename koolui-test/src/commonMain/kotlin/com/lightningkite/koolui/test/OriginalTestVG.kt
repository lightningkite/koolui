package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.*
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.image
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.withSizing
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.reacktive.property.DebugObservableProperty
import com.lightningkite.recktangle.Point

class OriginalTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Original Test"

    val stack = StandardObservableProperty<ViewFactory<VIEW>.() -> VIEW> {
        text(text = "Start").background(Color.blue.copy(alpha = .4f))
    }
    var num = 0

    val progress = DebugObservableProperty(0f)

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {

        vertical {

            -text(text = "Header", size = TextSize.Header)
            -text(text = "Subheader", size = TextSize.Subheader)
            -text(text = "Body", size = TextSize.Body)
            -text(text = "Tiny", size = TextSize.Tiny)
            -image(MaterialIcon.chevronLeft.color(Color.blue).withSizing(Point(48f, 48f)))
            -frame(text(text = "Framed text"))
            -button(label = progress.transform { "Progress: $it" }, onClick = {
                val currentValue = progress.value
                val newValue = (if(currentValue < 1f) {
                    currentValue + .3f
                } else {
                    0f
                }).coerceIn(0f, 1f)
                progress.value = newValue
            })
            -work(text("Done"), progress.transform { it < 1f })
            -progress(text("Done"), progress)
            -button(label = "Debug Info", onClick = {
                space(2f)
            })
            -button(label = ConstantObservableProperty("Button"), onClick = {
                stack.value = {
                    num++
                    text(text = "Number $num", size = TextSize.Header, align = AlignPair.CenterCenter)
                }
            })

            +swap(stack.transform {
                val animValues = Animation.values()
                it.invoke(dependency) to animValues[num % animValues.size]
            }, staticViewForSizing = text("asdf"))

        }
    }
}
