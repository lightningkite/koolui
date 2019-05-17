package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement

class VerticalTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Vertical"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical(
                LinearPlacement.wrapStart to text(text = "left", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapStart to text(text = "left", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapStart to text(text = "left", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", align = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", align = AlignPair.CenterCenter).background(Color.red)
        )
    }
}
