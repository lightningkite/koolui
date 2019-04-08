package com.lightningkite.koolui.preferences

actual object Preferences : Iterable<Pair<String, String>> {

    val cache: HashMap<String, String> = HashMap<String, String>()

    actual operator fun get(key: String): String? {
        return cache[key]
    }

    actual operator fun set(key: String, value: String?) {
        if (value == null) {
            cache.remove(key)
        } else {
            cache[key] = value
        }
    }

    actual override fun iterator(): Iterator<Pair<String, String>> = cache.entries.asSequence().map { it.key to it.value }.iterator()

    actual fun clear() {
        cache.clear()
    }

    fun String.escapeKey(): String = replace(" ", "\\ ").escapeValue()
    fun String.escapeValue(): String = replace("\\", "\\\\").replace("\n", "\\n")
    fun String.unescapeValue(): String = replace("\\\\", "\\").replace("\\n", "\n")
    fun String.unescapeKey(): String = unescapeValue().replace("\\ ", " ")
}
