package com.lightningkite.koolui

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.recktangle.Angle
import kotlin.browser.document
import kotlin.browser.window

actual object Location {
    actual val available: Boolean
        get() = window.navigator.asDynamic().geolocation != null

    actual fun requestOnce(
            reason: String,
            accuracyBetterThanMeters: Double,
            onRejected: () -> Unit,
            onResult: (LocationResult) -> Unit
    ) {
        window.navigator.asDynamic().geolocation?.getCurrentPosition(
                { position: dynamic ->
                    println("latitude = ${position.coords.latitude as Double}")
                    println("longitude = ${position.coords.longitude as Double}")
                    println("accuracyMeters = ${position.coords.accuracy as? Double ?: 100.0}")
                    println("altitudeMeters = ${position.coords.altitude as? Double ?: 0.0}")
                    println("altitudeAccuracyMeters = ${position.coords.altitudeAccuracy as? Double ?: 100.0}")
                    println("headingFromNorth = ${Angle.degrees(position.coords.heading as? Float ?: 0f)}")
                    println("speedMetersPerSecond = ${position.coords.speed as? Double ?: 0.0}")
                    onResult(LocationResult(
                            location = Geohash(
                                    latitude = position.coords.latitude as Double,
                                    longitude = position.coords.longitude as Double
                            ),
                            accuracyMeters = position.coords.accuracy as? Double ?: 100.0,
                            altitudeMeters = position.coords.altitude as? Double ?: 0.0,
                            altitudeAccuracyMeters = position.coords.altitudeAccuracy as? Double ?: 100.0,
                            headingFromNorth = Angle.degrees(position.coords.heading as? Float ?: 0f),
                            speedMetersPerSecond = position.coords.speed as? Double ?: 0.0
                    ))
                },
                { error: dynamic -> onRejected() }
        ) ?: onRejected()
    }

    actual fun requestOngoing(
            reason: String,
            accuracyBetterThanMeters: Double,
            onRejected: () -> Unit,
            onResult: (LocationResult) -> Unit
    ): Closeable {
        window.navigator.asDynamic().geolocation?.watchPosition(
                { position: dynamic ->
                    onResult(LocationResult(
                            location = Geohash(
                                    latitude = position.coords.latitude as Double,
                                    longitude = position.coords.longitude as Double
                            ),
                            accuracyMeters = position.coords.accuracy as Double,
                            altitudeMeters = position.coords.altitude as Double,
                            altitudeAccuracyMeters = position.coords.altitudeAccuracy as Double,
                            headingFromNorth = Angle.degrees(position.coords.heading as Float),
                            speedMetersPerSecond = position.coords.speed as Double
                    ))
                },
                { error: dynamic -> onRejected() }
        )
        return object : Closeable {
            override fun close() {
                window.navigator.asDynamic().geolocation?.clearWatch()
            }
        }
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