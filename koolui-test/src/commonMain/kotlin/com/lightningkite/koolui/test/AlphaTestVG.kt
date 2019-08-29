package com.lightningkite.koolui.test

import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.concepts.TextSize

class AlphaTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Alpha"
    val alpha = StandardObservableProperty(0f)

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {

        vertical {
            -button(label = ConstantObservableProperty("Change Alpha"), onClick = {
                alpha.value = if (alpha.value < .5f) 1f else 0f
            })
            -text(text = "Header", size = TextSize.Header).alpha(alpha)
        }
    }
}
