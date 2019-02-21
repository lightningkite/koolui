package com.lightningkite.koolui.preferences

import org.w3c.dom.get
import kotlin.browser.localStorage
import kotlin.browser.window

actual object Preferences : Iterable<Pair<String, String>> {
    actual operator fun get(key: String): String? {
        return window.localStorage.getItem(key)
    }

    actual operator fun set(key: String, value: String?) {
        if(value == null)
            window.localStorage.removeItem(key)
        else
            window.localStorage.setItem(key, value)
    }

    actual fun clear(){
        window.localStorage.clear()
    }

    actual override fun iterator(): Iterator<Pair<String, String>> = window.localStorage.let{ storage ->
        return (0 until storage.length).asSequence().map {
            val key = storage.key(it)!!
            key to storage.getItem(key)!!
        }.iterator()
    }
}
