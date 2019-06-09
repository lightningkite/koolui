package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.Event
import com.lightningkite.reacktive.property.ObservableProperty

expect object Location {
    val available: Boolean
    suspend fun requestOnce(
            reason: String,
            accuracyBetterThanMeters: Double = 100.0
    ): LocationResult

    suspend fun requestOngoing(
            reason: String,
            accuracyBetterThanMeters: Double = 100.0
    ): ObservableProperty<LocationResult>

    var getAddressImplementation: suspend (Geohash) -> String?
    suspend fun getAddress(from: Geohash): String?
    var getGeohashImplementation: suspend (String) -> Geohash?
    suspend fun getGeohash(from: String): Geohash?
}
