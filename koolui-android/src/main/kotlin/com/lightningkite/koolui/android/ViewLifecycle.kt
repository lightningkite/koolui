package com.lightningkite.koolui.android

import android.view.View
import com.lightningkite.koolui.implementationhelpers.AnyLifecycles
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty

var View.lifecycle: TreeObservableProperty
    get() = AnyLifecycles.getOrPut(this) { TreeObservableProperty() }
    set(value) {
        AnyLifecycles[this] = value
    }
