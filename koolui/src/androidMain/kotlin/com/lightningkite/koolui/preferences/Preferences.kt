package com.lightningkite.koolui.preferences

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.lightningkite.koolui.ApplicationAccess
import java.util.concurrent.ConcurrentHashMap

actual object Preferences : Iterable<Pair<String, String>> {

    val underlying by lazy { PreferenceManager.getDefaultSharedPreferences(ApplicationAccess.access!!.context) }
    val cache = ConcurrentHashMap<String, String>()

    actual operator fun get(key: String): String? {
        cache[key]?.let{ return it }
        underlying.getString(key, null)?.let {
            cache[key] = it
            return it
        }
        return null
    }

    actual operator fun set(key: String, value: String?) {
        if(value == null){
            cache.remove(key)
            underlying.edit().remove(key).apply()
        } else {
            cache[key] = value
            underlying.edit().putString(key, value).apply()
        }
    }
    actual override fun iterator(): Iterator<Pair<String, String>> = underlying.all.entries.asSequence().mapNotNull {
        val value = it.value as? String ?: return@mapNotNull null
        it.key!! to value
    }.iterator()

    actual fun clear() {
        underlying.edit().clear().apply()
    }
}