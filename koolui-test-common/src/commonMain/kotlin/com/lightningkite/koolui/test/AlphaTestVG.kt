package com.lightningkite.koolui.test

import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class AlphaTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Alpha"
    val alpha = StandardObservableProperty(0f)

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {

        vertical {
            -button(label = ConstantObservableProperty("Change Alpha"), onClick = {
                alpha.value = if (alpha.value < .5f) 1f else 0f
            })
            -text(text = "Header", size = TextSize.Header).alpha(alpha)
        }
    }
}
