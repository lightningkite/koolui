package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class VerticalTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Vertical"

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        vertical(
                LinearPlacement.wrapStart to text(text = "left", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapStart to text(text = "left", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapStart to text(text = "left", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapFill to text(text = "fill", alignPair = AlignPair.CenterCenter).background(Color.red),
                LinearPlacement.wrapEnd to text(text = "right", alignPair = AlignPair.CenterCenter).background(Color.red)
        )
    }
}
