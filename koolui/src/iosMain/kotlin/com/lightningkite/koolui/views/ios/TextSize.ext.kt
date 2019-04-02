package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.TextSize
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.labelFontSize

val TextSize.ios
    get() = when(this){
        TextSize.Tiny -> UIFont.labelFontSize * .75
        TextSize.Body -> UIFont.labelFontSize
        TextSize.Subheader -> UIFont.labelFontSize * 2
        TextSize.Header -> UIFont.labelFontSize * 4
    }
