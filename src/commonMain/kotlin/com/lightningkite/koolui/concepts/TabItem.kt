package com.lightningkite.koolui.concepts

import com.lightningkite.koolui.image.Image
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty

data class TabItem(
    var image: Image,
    var text: String,
    var description: String = "",
    var enabled: ObservableProperty<Boolean> = ConstantObservableProperty(true)
)
