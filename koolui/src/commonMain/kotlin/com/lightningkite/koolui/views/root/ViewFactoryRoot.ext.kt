package com.lightningkite.koolui.views.root

import com.lightningkite.koolui.views.ViewGenerator

fun <DEPENDENCY : ViewFactoryRoot<VIEW>, VIEW> DEPENDENCY.contentRoot(viewGenerator: ViewGenerator<DEPENDENCY, VIEW>): VIEW {
    return contentRoot(viewGenerator.generate(this))
}