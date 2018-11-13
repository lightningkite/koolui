package com.lightningkite.koolui.views.virtual

import com.lightningkite.kommon.collection.treeWalkDepthSequence
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty

abstract class View {
    var attached = TreeObservableProperty()
}

abstract class ContainerView : View() {
    abstract fun listViews(): List<View>

    fun recursiveViews(): Sequence<View> = listViews().asSequence().treeWalkDepthSequence {
        (it as? ContainerView)?.listViews()?.asSequence() ?: sequenceOf()
    }
}