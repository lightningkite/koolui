package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.kommon.collection.WeakHashMap
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty

class TreeObservableProperty() : ObservableProperty<Boolean>, MutableCollection<(Boolean) -> Unit> by ArrayList() {
    var parent: TreeObservableProperty? = null
        set(value) {
            if (value == this) throw IllegalArgumentException()
            field?.children?.remove(this)
            field = value
            value?.children?.add(this)
            broadcast()
        }
    var alwaysOn: Boolean = false
        set(value) {
            field = value
            broadcast()
        }
    override val value: Boolean get() = (parent?.value ?: alwaysOn)
    var previousValue: Boolean = value

    val children = ArrayList<TreeObservableProperty>()
    fun add(element: TreeObservableProperty): Boolean = children.add(element)
    fun remove(element: TreeObservableProperty): Boolean = children.remove(element)

    fun broadcast() {
        val newValue = value
        if (newValue != previousValue) {
            previousValue = newValue
            this.invokeAll(newValue)
            for (child in children) {
                child.broadcast()
            }
        }
    }
}
