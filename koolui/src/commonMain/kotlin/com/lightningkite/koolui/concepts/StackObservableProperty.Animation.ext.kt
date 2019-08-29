package com.lightningkite.koolui.concepts

import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.lastOrNullObservable

fun <T> ObservableList<T>.lastOrNullObservableWithAnimations(
    push: Animation = Animation.Push,
    pop: Animation = Animation.Pop,
    other: Animation = Animation.Fade
): ObservableProperty<Pair<T?, Animation>> {
    var current = this.size
    return this.lastOrNullObservable().transform {
        val newSize = this.size
        val result = it to when {
            newSize < current -> pop
            newSize == current -> other
            newSize > current -> push
            else -> other
        }
        current = newSize
        result
    }
}