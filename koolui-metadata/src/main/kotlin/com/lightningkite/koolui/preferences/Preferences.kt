package com.lightningkite.koolui.preferences

expect object Preferences:Iterable<Pair<String, String>> {
    operator fun get(key: String):String?
    operator fun set(key: String, value: String?)
    override fun iterator(): Iterator<Pair<String, String>>
    fun clear()
}
