package com.lightningkite.koolui.android

import android.view.View
import com.lightningkite.koolui.R
import com.lightningkite.koolui.implementationhelpers.AnyLifecycles
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty

var View.lifecycle: TreeObservableProperty
    get() {
        val existing = getTag(R.id.lifecycle) as? TreeObservableProperty
        if (existing != null) return existing
        val new = TreeObservableProperty()
        setTag(R.id.lifecycle, new)
        return new
    }
    set(value) {
        setTag(R.id.lifecycle, value)
    }
