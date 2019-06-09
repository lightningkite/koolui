package com.lightningkite.koolui.views

import com.lightningkite.koolui.builders.space

interface ViewGenerator<in DEPENDENCY, out VIEW> {
    val title: String get() = ""
    fun generate(dependency: DEPENDENCY): VIEW
    fun generateActions(dependency: DEPENDENCY): VIEW? = null

    companion object {
        //TODO: Re-enable inlining when Kotlin Native works with it
        /*inline*/ fun <DEPENDENCY, VIEW> make(
                title: String,
                /*crossinline*/ generate: DEPENDENCY.() -> VIEW
        ) = object : ViewGenerator<DEPENDENCY, VIEW> {
            override val title: String = title
            override fun generate(dependency: DEPENDENCY): VIEW = generate.invoke(dependency)
        }

        object Empty : ViewGenerator<ViewFactory<Any?>, Any?> {
            override fun generate(dependency: ViewFactory<Any?>): Any? = dependency.space(0f)
        }

        @Suppress("UNCHECKED_CAST")
        fun <DEPENDENCY : ViewFactory<VIEW>, VIEW> empty() = Empty as ViewGenerator<DEPENDENCY, VIEW>
    }

}
