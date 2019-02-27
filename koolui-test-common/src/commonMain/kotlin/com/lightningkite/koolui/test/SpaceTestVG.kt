package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.recktangle.Point

class SpaceTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Space"
    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        space(Point(32f, 32f))
    }
}
