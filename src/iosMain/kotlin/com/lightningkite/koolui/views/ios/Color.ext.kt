package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.color.Color
import platform.UIKit.UIColor

val Color.ios
    get() = UIColor.colorWithRed(
            red = red.toDouble(),
            green = green.toDouble(),
            blue = blue.toDouble(),
            alpha = alpha.toDouble()
    )
