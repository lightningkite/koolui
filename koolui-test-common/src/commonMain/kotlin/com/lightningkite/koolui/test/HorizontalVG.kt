package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class HorizontalVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Horizontal"

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        horizontal(
                LinearPlacement.fillStart to text(text = "left", alignPair = AlignPair.CenterCenter),
                LinearPlacement.fillFill to text(text = "fill", alignPair = AlignPair.CenterCenter),
                LinearPlacement.fillEnd to text(text = "right", alignPair = AlignPair.CenterCenter)
        )
    }
}
