package com.lightningkite.koolui.concepts

import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty

data class TabItem(
        var imageWithOptions: ImageWithOptions,
        var text: String,
        var description: String = "",
        var enabled: ObservableProperty<Boolean> = ConstantObservableProperty(true)
)
