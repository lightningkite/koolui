package com.lightningkite.koolui.preferences

import java.io.File

actual object Preferences : Iterable<Pair<String, String>> {

    lateinit var file: File
    val cache: HashMap<String, String> = HashMap<String, String>()

    fun init(file: File) {
        this.file = file
        if (!file.exists()) {
            file.createNewFile()
        }
        //load from file
        file.readText().split('\n').forEach {
            val key = it.substringBefore(' ').unescapeKey()
            val value = it.substringAfter(' ').unescapeValue()
            cache[key] = value
        }
    }

    actual operator fun get(key: String): String? {
        return cache[key]
    }

    actual operator fun set(key: String, value: String?) {
        if (value == null) {
            cache.remove(key)
        } else {
            cache[key] = value
        }
        file.writeText(cache.entries.joinToString("\n") {
            it.key.escapeKey() + " " + it.value.escapeValue()
        })
    }

    actual override fun iterator(): Iterator<Pair<String, String>> = cache.entries.asSequence().map { it.key to it.value }.iterator()

    actual fun clear() {
        cache.clear()
        file.writeText("")
    }

    fun String.escapeKey(): String = replace(" ", "\\ ").escapeValue()
    fun String.escapeValue(): String = replace("\\", "\\\\").replace("\n", "\\n")
    fun String.unescapeValue(): String = replace("\\\\", "\\").replace("\\n", "\n")
    fun String.unescapeKey(): String = unescapeValue().replace("\\ ", " ")
}
