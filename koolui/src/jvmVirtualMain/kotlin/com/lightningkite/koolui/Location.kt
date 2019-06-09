package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty

actual object Location {
    actual val available: Boolean
        get() = true

    var mockLocation = StandardObservableProperty(LocationResult(Geohash(0)))
    var shouldReject = false

    actual suspend fun requestOnce(reason: String, accuracyBetterThanMeters: Double): LocationResult {
        if(shouldReject)
            throw ForbiddenException()
        else
            return mockLocation.value
    }

    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> {
        if(shouldReject)
            throw ForbiddenException()
        else
            return mockLocation
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