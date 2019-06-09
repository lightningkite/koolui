package com.lightningkite.koolui

import android.annotation.SuppressLint
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.recktangle.Angle
import android.content.Context
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.koolui.async.UI
import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual object Location {

    val locationManager
        get() = ApplicationAccess.access?.context
                ?.getSystemService(Context.LOCATION_SERVICE)
                ?.let { it as? LocationManager }

    actual val available: Boolean
        get() = locationManager?.let {
            it.isProviderEnabled(LocationManager.GPS_PROVIDER) || it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } ?: false

    @SuppressLint("MissingPermission")
    actual suspend fun requestOnce(reason: String, accuracyBetterThanMeters: Double): LocationResult = suspendCoroutine { cont ->
        ApplicationAccess.access?.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it) {
                val criteria = Criteria()
                criteria.horizontalAccuracy = when (accuracyBetterThanMeters) {
                    in 0f..100f -> Criteria.ACCURACY_HIGH
                    in 100f..500f -> Criteria.ACCURACY_MEDIUM
                    else -> Criteria.ACCURACY_LOW
                }
                locationManager!!.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: android.location.Location) {
                        cont.resume(LocationResult(
                                location = Geohash(location.latitude, location.longitude),
                                accuracyMeters = location.accuracy.toDouble(),
                                altitudeMeters = location.altitude,
                                altitudeAccuracyMeters = 100.0,
                                headingFromNorth = Angle.degrees(location.bearing),
                                speedMetersPerSecond = location.speed.toDouble()
                        ))
                    }

                    override fun onProviderDisabled(p0: String?) {
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                    }

                    override fun onProviderEnabled(p0: String?) {
                    }
                }, Looper.getMainLooper())
            } else {
                cont.resumeWithException(ForbiddenException())
            }
        }
    }

    @SuppressLint("MissingPermission")
    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> = suspendCoroutine<ObservableProperty<LocationResult>> { cont ->
        val event = object : EnablingMutableCollection<(LocationResult) -> Unit>(), ObservableProperty<LocationResult> {
            val obs = this
            val listener = object : LocationListener {
                override fun onLocationChanged(location: android.location.Location) {
                    if (!returned) {
                        returned = true
                        cont.resume(obs)
                    }
                    value = LocationResult(
                            location = Geohash(location.latitude, location.longitude),
                            accuracyMeters = location.accuracy.toDouble(),
                            altitudeMeters = location.altitude,
                            altitudeAccuracyMeters = 100.0,
                            headingFromNorth = Angle.degrees(location.bearing),
                            speedMetersPerSecond = location.speed.toDouble()
                    )
                    invokeAll(value)
                }

                override fun onProviderDisabled(p0: String?) {
                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                }

                override fun onProviderEnabled(p0: String?) {
                }
            }

            override lateinit var value: LocationResult
            var returned = false

            var actuallyListening = false
            override fun enable() {
                if (actuallyListening) return
                actuallyListening = true
                ApplicationAccess.access?.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (it) {
                        val criteria = Criteria()
                        criteria.horizontalAccuracy = accuracyBetterThanMeters.toInt()
                        locationManager?.requestLocationUpdates(
                                1000L,
                                10.0f,
                                criteria,
                                listener,
                                Looper.getMainLooper()
                        )
                    } else {
                        if (!returned) {
                            returned = true
                            cont.resumeWithException(ForbiddenException())
                        }
                    }
                }
            }

            override fun disable() {
                GlobalScope.launch(Dispatchers.UI) {
                    delay(1000)
                    if (size == 0) {
                        locationManager?.removeUpdates(listener)
                        actuallyListening = false
                    }
                }
            }
        }
        event.enable()
    }

    private var getAddressImplementationAtomic: AtomicReference<suspend (Geohash) -> String?> = AtomicReference { input ->
        GlobalScope.async {
            ApplicationAccess.access?.run {
                Geocoder(context).getFromLocation(input.latitude, input.longitude, 1).firstOrNull()?.let { address ->
                    (0..address.maxAddressLineIndex).joinToString("\n") { address.getAddressLine(it) }
                }
            }
        }.await()
    }
    actual var getAddressImplementation: suspend (Geohash) -> String? by getAddressImplementationAtomic
    actual suspend fun getAddress(from: Geohash): String? = getAddressImplementation(from)

    private var getGeohashImplementationAtomic: AtomicReference<suspend (String) -> Geohash?> = AtomicReference { input ->
        GlobalScope.async {
            ApplicationAccess.access?.run {
                Geocoder(context).getFromLocationName(input, 1).firstOrNull()?.let { address ->
                    Geohash(latitude = address.latitude, longitude = address.longitude)
                }
            }
        }.await()
    }
    actual var getGeohashImplementation: suspend (String) -> Geohash? by getGeohashImplementationAtomic
    actual suspend fun getGeohash(from: String): Geohash? = getGeohashImplementation(from)
}