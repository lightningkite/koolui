package com.lightningkite.koolui

import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.property.ObservableProperty

actual object Location {
    actual val available: Boolean
        get() = false

    actual suspend fun requestOnce(reason: String, accuracyBetterThanMeters: Double): LocationResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual var getAddressImplementation: suspend (Geohash) -> String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    actual suspend fun getAddress(from: Geohash): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual var getGeohashImplementation: suspend (String) -> Geohash?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    actual suspend fun getGeohash(from: String): Geohash? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
