package com.lightningkite.koolui.test

import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.builders.pagesEmbedded
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class PagesVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Pages"

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        pagesEmbedded(
                dependency,
                StandardObservableProperty(0),
                {
                    text(size = TextSize.Header, text = "First page", alignPair = AlignPair.CenterCenter)
                },
                {
                    text(size = TextSize.Header, text = "Second page", alignPair = AlignPair.CenterCenter)
                },
                {
                    text(size = TextSize.Header, text = "Third page", alignPair = AlignPair.CenterCenter)
                },
                {
                    text(size = TextSize.Header, text = "Last page", alignPair = AlignPair.CenterCenter)
                }
        )
    }
}
