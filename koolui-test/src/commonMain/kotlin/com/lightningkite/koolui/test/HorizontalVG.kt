package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement

class HorizontalVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Horizontal"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        horizontal(
                LinearPlacement.fillStart to text(text = "left", align = AlignPair.CenterCenter),
                LinearPlacement.fillFill to text(text = "fill", align = AlignPair.CenterCenter),
                LinearPlacement.fillEnd to text(text = "right", align = AlignPair.CenterCenter)
        )
    }
}
