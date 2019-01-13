package com.lightningkite.koolui.views

interface ViewGenerator<in DEPENDENCY, out VIEW> {
    val title: String get() = ""
    fun generate(dependency: DEPENDENCY): VIEW
    fun generateActions(dependency: DEPENDENCY): VIEW? = null

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