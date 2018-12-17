package com.lightningkite.koolui.views

import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.ObservableListListenerSet
import com.lightningkite.reacktive.list.lifecycle.bind
import com.lightningkite.reacktive.property.ObservableProperty
import javafx.collections.FXCollections


fun <T> ObservableList<T>.asJavaFX(
    lifecycle: ObservableProperty<Boolean>
): javafx.collections.ObservableList<T> {
    val backing = this
    val javafx = FXCollections.observableArrayList<T>()

    backing.bindToJavaFX(lifecycle, javafx)

    return javafx
}

fun <T> ObservableList<T>.bindToJavaFX(
    lifecycle: ObservableProperty<Boolean>,
    javafx: javafx.collections.ObservableList<T>
) {
    val backing = this

    lifecycle.bind(backing, ObservableListListenerSet(
        onAddListener = { item, position ->
            javafx.add(position, item)
        },
        onRemoveListener = { item, position ->
            javafx.removeAt(position)
        },
        onChangeListener = { old, item, position ->
            javafx[position] = item
        },
        onMoveListener = { _, oldPosition, position ->
            val item = javafx[oldPosition]
            val swapWith = javafx[position]
            javafx[oldPosition] = swapWith
            javafx[position] = item
        },
        onReplaceListener = { list ->
            javafx.setAll(list)
        }
    ))

    javafx.setAll(backing)
    lifecycle.add {
        if (it) {
            javafx.setAll(backing)
        }
    }
}