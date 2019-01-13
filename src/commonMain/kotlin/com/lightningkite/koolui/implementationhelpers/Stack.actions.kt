package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.koolui.concepts.TabItem
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.lastOrNullObservable
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.sub

fun <A, B> ObservableList<ViewGenerator<A, B>>.actions(): ObservableProperty<ObservableList<Pair<TabItem, suspend () -> Unit>>> = lastOrNullObservable().sub {
    val result: ObservableProperty<ObservableList<Pair<TabItem, suspend () -> Unit>>> = it?.actions?.onListUpdate ?: ConstantObservableProperty(observableListOf())
    result
}