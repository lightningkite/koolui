package com.lightningkite.koolui.test

import com.lightningkite.kommon.collection.*
import com.lightningkite.reacktive.list.observableListOf
import com.lightningkite.reacktive.list.WrapperObservableList
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class MainVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "KotlinX UI Test"

    val stack = WrapperObservableList<MyViewGenerator<VIEW>>()

    init {
        //Startup
        stack.push(SelectorVG(stack))
    }

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        window(
                dependency = dependency,
                stack = stack,
                tabs = listOf()
        )
    }
}
