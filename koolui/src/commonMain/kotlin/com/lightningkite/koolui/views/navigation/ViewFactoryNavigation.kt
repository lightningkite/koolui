package com.lightningkite.koolui.views.navigation

import com.lightningkite.koolui.concepts.TabItem
import com.lightningkite.koolui.geometry.Direction
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.list.MutableObservableList
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty

interface ViewFactoryNavigation<VIEW> {

    /**
     * The main window of the program - provides a stack and tabs, also hosting the actions for the given view generator.
     */
    fun <DEPENDENCY> window(
            dependency: DEPENDENCY,
            stack: MutableObservableList<ViewGenerator<DEPENDENCY, VIEW>>,
            tabs: List<Pair<TabItem, ViewGenerator<DEPENDENCY, VIEW>>>
    ): VIEW// = defaultSmallWindow(theme, withColorSet(theme.bar), dependency, stack, tabs)

    /**
     * A set of ordered pages you can swap through with the built-in navigator.
     */
    fun <DEPENDENCY> pages(
            dependency: DEPENDENCY,
            page: MutableObservableProperty<Int>,
            vararg pageGenerator: ViewGenerator<DEPENDENCY, VIEW>
    ): VIEW// = defaultPages(colorSet.foreground, dependency, page, *pageGenerator)

    /**
     * A set of tabs, with one selected.
     * Does not modify any views outside of it - hook it up to a [swap] for that functionality.
     */
    fun tabs(
            options: ObservableList<TabItem>,
            selected: MutableObservableProperty<TabItem>
    ): VIEW// = defaultTabs(options, selected)


    //Collection Views

    /**
     * Shows a list of items and notifies you when it's scrolled to the end.
     */
    fun <T> list(
            data: ObservableList<T>,
            firstIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            lastIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
            direction: Direction = Direction.Down,
            makeView: (item: ObservableProperty<T>, index: ObservableProperty<Int>) -> VIEW
    ): VIEW// = defaultList(10, colorSet.foreground, data, direction, firstIndex, lastIndex, makeView)


}