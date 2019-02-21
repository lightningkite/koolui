package com.lightningkite.koolui

import com.lightningkite.koolui.implementationhelpers.AnyLifecycles
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import org.w3c.dom.HTMLElement


var HTMLElement.lifecycle
    get() = AnyLifecycles.getOrPut(this) { TreeObservableProperty() }
    set(value) {
        AnyLifecycles[this] = value
    }