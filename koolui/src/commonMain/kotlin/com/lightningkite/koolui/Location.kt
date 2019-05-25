package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.lokalize.location.Geohash

expect object Location {
    val available: Boolean
    fun requestOnce(
            reason: String,
            accuracyBetterThanMeters: Double = 100.0,
            onRejected: () -> Unit = {},
            onResult: (LocationResult) -> Unit
    )

    fun requestOngoing(
            reason: String,
            accuracyBetterThanMeters: Double = 100.0,
            onRejected: () -> Unit = {},
            onResult: (LocationResult) -> Unit
    ): Closeable

    var getAddressImplementation: suspend (Geohash) -> String?
    suspend fun getAddress(from: Geohash): String?
    var getGeohashImplementation: suspend (String) -> Geohash?
    suspend fun getGeohash(from: String): Geohash?
}
