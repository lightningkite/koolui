package com.lightningkite.koolui.test

import com.lightningkite.recktangle.Point
import com.lightningkite.reacktive.list.WrapperObservableList
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.horizontal
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.asImage
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class IconsTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "IconsTest"

    val tests = WrapperObservableList(MaterialIcon.values().toMutableList())

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        list(data = tests, makeView = { itemObs ->
            horizontal {
                - image(itemObs.transform { it.color(Color.blue).asImage(Point(48f, 48f)) })
                + text(itemObs.transform { it.name })
            }

        }).margin(8f)
    }
}
