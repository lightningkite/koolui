package com.lightningkite.koolui

import com.lightningkite.kommon.atomic.AtomicReference
import com.lightningkite.kommon.exception.ForbiddenException
import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Angle
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.darwin.NSObject
import kotlin.coroutines.resumeWithException

actual object Location {


    actual val available: Boolean
        get() = false

    actual suspend fun requestOnce(reason: String, accuracyBetterThanMeters: Double): LocationResult = suspendCancellableCoroutine { continuation ->

        when (CLLocationManager.authorizationStatus()) {
            kCLAuthorizationStatusDenied -> {
                continuation.resumeWithException(ForbiddenException("User has rejected permission to access location"))
                return@suspendCancellableCoroutine
            }
            kCLAuthorizationStatusRestricted -> {
                continuation.resumeWithException(ForbiddenException("User cannot give permission to access location due to parental restriction"))
                return@suspendCancellableCoroutine
            }
        }

        var requestOccured = false
        val manager = CLLocationManager()

        val delegate: CLLocationManagerDelegateProtocol = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
                if (!requestOccured) return
                when (didChangeAuthorizationStatus) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        manager.requestLocation()
                    }
                    kCLAuthorizationStatusDenied -> {
                        continuation.resumeWithException(ForbiddenException("User has rejected permission to access location"))
                    }
                    kCLAuthorizationStatusRestricted -> {
                        continuation.resumeWithException(ForbiddenException("User cannot give permission to access location due to parental restriction"))
                    }
                    else -> {
                        continuation.resumeWithException(ForbiddenException("User has not given permission to access location"))
                    }
                }
            }

            override fun locationManager(manager: CLLocationManager, didUpdateToLocation: CLLocation, fromLocation: CLLocation) {
                continuation.resume(
                        value = LocationResult(
                                location = Geohash(
                                        latitude = didUpdateToLocation.coordinate.useContents { latitude },
                                        longitude = didUpdateToLocation.coordinate.useContents { longitude }
                                ),
                                accuracyMeters = didUpdateToLocation.horizontalAccuracy,
                                altitudeMeters = didUpdateToLocation.altitude,
                                altitudeAccuracyMeters = didUpdateToLocation.verticalAccuracy,
                                headingFromNorth = Angle.degrees(didUpdateToLocation.course.toFloat()) + Angle.quarterCircle,
                                speedMetersPerSecond = didUpdateToLocation.speed
                        ),
                        onCancellation = { }
                )
            }
        }
        manager.delegate = delegate
        manager.requestWhenInUseAuthorization()
        requestOccured = true
    }

    actual suspend fun requestOngoing(reason: String, accuracyBetterThanMeters: Double): ObservableProperty<LocationResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val getAddressImplementationAtomic = AtomicReference<suspend (Geohash) -> String?> {
        suspendCancellableCoroutine<String?> { continuation ->
            val geocoder = CLGeocoder()
            geocoder.reverseGeocodeLocation(
                    location = CLLocation(latitude = it.latitude, longitude = it.longitude),
                    completionHandler = { list, error ->
                        if (error != null) {
                            continuation.resumeWithException(Exception(error.localizedDescription))
                        } else {
                            continuation.resume(
                                    value = list?.firstOrNull()?.let {
                                        (it as CLPlacemark).run {
                                            "$name $locality, $administrativeArea $postalCode $country"
                                        }
                                    },
                                    onCancellation = {}
                            )
                        }
                    }
            )
        }
    }
    actual var getAddressImplementation: suspend (Geohash) -> String? by getAddressImplementationAtomic
    actual suspend fun getAddress(from: Geohash): String? = getAddressImplementation(from)

    val getGeohashImplementationAtomic = AtomicReference<suspend (String) -> Geohash?> {
        suspendCancellableCoroutine<Geohash?> { continuation ->
            val geocoder = CLGeocoder()
            geocoder.geocodeAddressString(
                    addressString = it,
                    completionHandler = { list, error ->
                        if (error != null) {
                            continuation.resumeWithException(Exception(error.localizedDescription))
                        } else {
                            continuation.resume(
                                    value = list?.firstOrNull()?.let {
                                        val placemark = (it as CLPlacemark)
                                        Geohash(
                                                latitude = placemark.location?.coordinate?.useContents { latitude }
                                                        ?: return@let null,
                                                longitude = placemark.location?.coordinate?.useContents { longitude }
                                                        ?: return@let null
                                        )
                                    },
                                    onCancellation = {}
                            )
                        }
                    }
            )
        }
    }
    actual var getGeohashImplementation: suspend (String) -> Geohash? by getGeohashImplementationAtomic
    actual suspend fun getGeohash(from: String): Geohash? = getGeohash(from)
}
