package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.*
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.list.WrapperObservableList
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class MainVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "KotlinX UI Test"

    val stack = WrapperObservableList<ViewGenerator<ViewFactory<VIEW>, VIEW>>()

    init {
        //Startup
        stack.push(SelectorVG(stack))
    }

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        window(
                dependency = dependency,
                stack = stack,
                tabs = listOf(),
                actions = observableListOf()
        )
    }
}