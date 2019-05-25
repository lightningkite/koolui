package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.lokalize.location.Geohash

actual object Location {
    actual val available: Boolean
        get() = true

    var mockLocation: LocationResult = LocationResult(Geohash(0))
    var shouldReject = false

    actual fun requestOnce(reason: String, accuracyBetterThanMeters: Double, onRejected: () -> Unit, onResult: (LocationResult) -> Unit) {
        if(shouldReject)
            onRejected()
        else
            onResult(mockLocation)
    }

    actual fun requestOngoing(reason: String, accuracyBetterThanMeters: Double, onRejected: () -> Unit, onResult: (LocationResult) -> Unit): Closeable {
        if(shouldReject)
            onRejected()
        else
            onResult(mockLocation)
        return Closeable {  }
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