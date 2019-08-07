package com.lightningkite.koolui.views

import com.lightningkite.reacktive.Event0
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point

interface Touch {
    val position: ObservableProperty<Point>
    val onRelease: Event0

    class Impl : Touch {
        override val position = StandardObservableProperty(Point())
        override val onRelease = ArrayList<()->Unit>()
    }
}