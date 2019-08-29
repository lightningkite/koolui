package com.lightningkite.koolui.views.navigation

import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty


/**
 * Shows a list of items and notifies you when it's scrolled to the end.
 */
fun <VIEW, T> ViewFactoryNavigation<VIEW>.list(
        data: ObservableList<T>,
        firstIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
        lastIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
        direction: Direction = Direction.Down,
        makeView: (obs: ObservableProperty<T>) -> VIEW
): VIEW = list(data, firstIndex, lastIndex, direction) { obs, index -> makeView(obs) }


fun <DEPENDENCY, VIEW> ViewFactoryNavigation<VIEW>.pagesEmbedded(
        dependency: DEPENDENCY,
        page: MutableObservableProperty<Int>,
        vararg pageGenerators: (DEPENDENCY) -> VIEW
) = pages(dependency, page, *pageGenerators.map {
    object : ViewGenerator<DEPENDENCY, VIEW> {
        @Suppress("UNCHECKED_CAST")
        override fun generate(dependency: DEPENDENCY): VIEW = it(dependency)
    }
}.toTypedArray())