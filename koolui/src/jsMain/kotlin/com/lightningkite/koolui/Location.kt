package com.lightningkite.koolui

import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.koolui.async.UI
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Angle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.browser.window
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual object Location {
    actual val available: Boolean
        get() = window.navigator.asDynamic().geolocation != null

    actual suspend fun requestOnce(
            reason: String,
            accuracyBetterThanMeters: Double
    ): LocationResult = suspendCoroutine { continuation ->
        window.navigator.asDynamic().geolocation?.getCurrentPosition(
                { position: dynamic ->
                    println("latitude = ${position.coords.latitude as Double}")
                    println("longitude = ${position.coords.longitude as Double}")
                    println("accuracyMeters = ${position.coords.accuracy as? Double ?: 100.0}")
                    println("altitudeMeters = ${position.coords.altitude as? Double ?: 0.0}")
                    println("altitudeAccuracyMeters = ${position.coords.altitudeAccuracy as? Double ?: 100.0}")
                    println("headingFromNorth = ${Angle.degrees(position.coords.heading as? Float ?: 0f)}")
                    println("speedMetersPerSecond = ${position.coords.speed as? Double ?: 0.0}")
                    continuation.resume(LocationResult(
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
                { error: dynamic -> continuation.resumeWithException(throw ForbiddenException()) }
        ) as? Unit ?: continuation.resumeWithException(throw ForbiddenException())
    }

    var subCount = 0
    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> = suspendCoroutine { cont ->
        val event = object: EnablingMutableCollection<(LocationResult)->Unit>(), ObservableProperty<LocationResult>{
            override lateinit var value: LocationResult
            var returned = false

            var id = 0
            var actuallyListening = false
            override fun enable() {
                if(actuallyListening) return
                actuallyListening = true
                id = window.navigator.asDynamic().geolocation?.watchPosition(
                        { position: dynamic ->
                            if(!returned) {
                                returned = true
                                cont.resume(this)
                            }
                            value = LocationResult(
                                    location = Geohash(
                                            latitude = position.coords.latitude as Double,
                                            longitude = position.coords.longitude as Double
                                    ),
                                    accuracyMeters = position.coords.accuracy as Double,
                                    altitudeMeters = position.coords.altitude as Double,
                                    altitudeAccuracyMeters = position.coords.altitudeAccuracy as Double,
                                    headingFromNorth = Angle.degrees(position.coords.heading as Float),
                                    speedMetersPerSecond = position.coords.speed as Double
                            )
                            invokeAll(value)
                            Unit
                        },
                        { error: dynamic ->
                            if(returned) {
                                println("Location error: $error")
                            } else {
                                returned = true
                                cont.resumeWithException(Exception(error.toString()))
                            }
                        }
                ) as Int
            }

            override fun disable() {
                GlobalScope.launch(Dispatchers.UI){
                    delay(1000)
                    if(size == 0) {
                        window.navigator.asDynamic().geolocation?.clearWatch(id)
                        actuallyListening = false
                    }
                }
            }
        }
        event.enable()
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