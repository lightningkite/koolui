package com.lightningkite.koolui

import com.lightningkite.lokalize.location.Geohash

suspend fun String.getGeohash() = Location.getGeohash(this)
suspend fun Geohash.getAddress() = Location.getAddress(this)