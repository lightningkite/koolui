package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.property.ObservableProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual object Location {
    actual val available: Boolean
        get() = false

    actual suspend fun requestOnce(reason: String, accuracyBetterThanMeters: Double): LocationResult = throw UnsupportedOperationException()

    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> = throw UnsupportedOperationException()


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