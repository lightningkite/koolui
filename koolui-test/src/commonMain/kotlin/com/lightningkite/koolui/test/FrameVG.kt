package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair

class FrameVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Frame"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        align(
                AlignPair.TopLeft to text(text = "Top Left", size = TextSize.Body),
                AlignPair.CenterLeft to text(text = "Center Left", size = TextSize.Body),
                AlignPair.BottomLeft to text(text = "Bottom Left", size = TextSize.Body),
                AlignPair.TopCenter to text(text = "Top Center", size = TextSize.Body),
                AlignPair.CenterCenter to text(text = "Center Center", size = TextSize.Body),
                AlignPair.FillFill to text(text = "Fill", size = TextSize.Header, align = AlignPair.TopLeft),
                AlignPair.BottomCenter to text(text = "Bottom Center", size = TextSize.Body),
                AlignPair.TopRight to text(text = "Top Right", size = TextSize.Body),
                AlignPair.CenterRight to text(text = "Center Right", size = TextSize.Body),
                AlignPair.BottomRight to text(text = "Bottom Right", size = TextSize.Body)
        )
    }
}
