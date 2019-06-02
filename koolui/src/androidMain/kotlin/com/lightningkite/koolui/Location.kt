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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

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
    actual fun requestOnce(
            reason: String,
            accuracyBetterThanMeters: Double,
            onRejected: () -> Unit,
            onResult: (LocationResult) -> Unit
    ) {
        ApplicationAccess.access?.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it) {
                val criteria = Criteria()
                criteria.horizontalAccuracy = when(accuracyBetterThanMeters){
                    in 0f..100f -> Criteria.ACCURACY_HIGH
                    in 100f..500f-> Criteria.ACCURACY_MEDIUM
                    else -> Criteria.ACCURACY_LOW
                }
                locationManager!!.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: android.location.Location) {
                        onResult(LocationResult(
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
                onRejected()
            }
        }
    }

    @SuppressLint("MissingPermission")
    actual fun requestOngoing(
            reason: String,
            accuracyBetterThanMeters: Double,
            onRejected: () -> Unit,
            onResult: (LocationResult) -> Unit
    ): Closeable {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                onResult(LocationResult(
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
        }
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
                onRejected()
            }
        }

        return object : Closeable {
            override fun close() {
                locationManager?.removeUpdates(listener)
            }
        }
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