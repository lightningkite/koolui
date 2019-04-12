package com.lightningkite.koolui.test

import com.lightningkite.recktangle.Point
import com.lightningkite.reacktive.list.WrapperObservableList
import com.lightningkite.reacktive.property.transform
import com.lightningkite.koolui.builders.horizontal
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.image.MaterialIcon
import com.lightningkite.koolui.image.withSizing
import com.lightningkite.koolui.image.color
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class IconsTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "IconsTest"

    val tests = WrapperObservableList(MaterialIcon.values().toMutableList())

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        list(data = tests, makeView = { itemObs ->
            horizontal {
                -image(itemObs.transform { it.color(Color.blue).withSizing(Point(48f, 48f)) })
                +text(itemObs.transform { it.name })
            }

        }).margin(8f)
    }
}
