package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.*
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.image
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.asImage
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.recktangle.Point

class OriginalTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Original Test"

    val stack = StandardObservableProperty<ViewFactory<VIEW>.() -> VIEW> {
        text(text = "Start")
    }
    var num = 0

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {

        vertical {

            -text(text = "Header", size = TextSize.Header)
            -text(text = "Subheader", size = TextSize.Subheader)
            -text(text = "Body", size = TextSize.Body)
            -text(text = "Tiny", size = TextSize.Tiny)
            -image(MaterialIcon.chevronLeft.color(Color.blue).asImage(Point(48f, 48f)))
            -progress(space(Point(24f, 24f)), ConstantObservableProperty(.5f))
            -work(space(Point(24f, 24f)), ConstantObservableProperty(true))
            -button(label = ConstantObservableProperty("Button"), onClick = {
                stack.value = {
                    num++
                    text(text = "Number $num", size = TextSize.Header, alignPair = AlignPair.CenterCenter)
                }
            })

            +swap(stack.transform {
                val animValues = Animation.values()
                it.invoke(dependency) to animValues[num % animValues.size]
            })

        }
    }
}
