package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.kommon.collection.WeakHashMap

data class DesiredMargins(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    constructor(all: Float) : this(all, all, all, all)
}

val AnyDesiredMargins = WeakHashMap<Any, DesiredMargins>()
