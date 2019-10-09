package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.push
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.ExternalAccess
import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Animation
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

class SwapTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Swap Test"

    val size = StandardObservableProperty(0)

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -button("Change Sizes"){
                this@SwapTestVG.size.value = (this@SwapTestVG.size.value + 2) % 20
            }
            -text("above ".repeat(8))
            -scrollHorizontal(swap(this@SwapTestVG.size.transform {
                text("words ".repeat(it)) to Animation.None
            }))
            -text("below ".repeat(8))
        }
    }
}

