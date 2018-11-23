package com.lightningkite.koolui.preferences

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.lightningkite.koolui.ApplicationAccess

actual object Preferences : Iterable<Pair<String, String>> {

    val underlying by lazy { PreferenceManager.getDefaultSharedPreferences(ApplicationAccess.access!!.context) }

    actual operator fun get(key: String): String? = underlying.getString(key, null)

    actual operator fun set(key: String, value: String?) {
        if(value == null){
            underlying.edit().remove(key).apply()
        } else {
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