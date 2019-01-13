package com.lightningkite.koolui.views

import com.lightningkite.koolui.concepts.TabItem
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.observableListOf

interface ViewGenerator<in DEPENDENCY, out VIEW> {
    val title: String get() = ""
    fun generate(dependency: DEPENDENCY): VIEW
    val actions: ObservableList<Pair<TabItem, suspend () -> Unit>> get() = observableListOf()

    companion object {
        inline fun <DEPENDENCY, VIEW> make(
            title: String,
            crossinline generate: (DEPENDENCY) -> VIEW
        ) = object : ViewGenerator<DEPENDENCY, VIEW> {
            override val title: String = title
            override fun generate(dependency: DEPENDENCY): VIEW = generate.invoke(dependency)
        }
    }
}