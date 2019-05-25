package com.lightningkite.koolui

import com.lightningkite.lokalize.location.Geohash
import com.lightningkite.recktangle.Angle

data class LocationResult(
        val location: Geohash,
        val accuracyMeters: Double = 100.0,
        val altitudeMeters: Double = 0.0,
        val altitudeAccuracyMeters: Double = 100.0,
        val headingFromNorth: Angle = Angle(0f),
        val speedMetersPerSecond: Double = 0.0
)