package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.lokalize.location.Geohash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual object Location {
    actual val available: Boolean
        get() = false

    actual fun requestOnce(reason: String, accuracyBetterThanMeters: Double, onRejected: () -> Unit, onResult: (LocationResult) -> Unit) {
        onRejected()
    }

    actual fun requestOngoing(reason: String, accuracyBetterThanMeters: Double, onRejected: () -> Unit, onResult: (LocationResult) -> Unit): Closeable {
        onRejected()
        return Closeable { }
    }

    private var getAddressImplementationAtomic: AtomicReference<suspend (Geohash) -> String?> = AtomicReference { input ->
        null
    }
    actual var getAddressImplementation: suspend (Geohash) -> String? by getAddressImplementationAtomic
    actual suspend fun getAddress(from: Geohash): String? = getAddressImplementation(from)

    private var getGeohashImplementationAtomic: AtomicReference<suspend (String) -> Geohash?> = AtomicReference { input ->
        null
    }
    actual var getGeohashImplementation: suspend (String) -> Geohash? by getGeohashImplementationAtomic
    actual suspend fun getGeohash(from: String): Geohash? = getGeohashImplementation(from)
}