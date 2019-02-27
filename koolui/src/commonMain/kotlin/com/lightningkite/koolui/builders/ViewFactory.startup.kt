package com.lightningkite.koolui.builders

import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

fun <DEPENDENCY : ViewFactory<VIEW>, VIEW> DEPENDENCY.contentRoot(viewGenerator: ViewGenerator<DEPENDENCY, VIEW>): VIEW {
    return contentRoot(viewGenerator.generate(this))
}