package com.lightningkite.koolui

import com.lightningkite.kommon.string.Uri
import com.lightningkite.lokalize.location.Geohash

fun ExternalAccess.openGeohash(geohash: Geohash){
    when (UIPlatform.current) {
        UIPlatform.IOS -> ExternalAccess.openUri(Uri("http://maps.apple.com/?q=Place&ll=${geohash.latitude},${geohash.longitude}"))
        UIPlatform.Android,
        UIPlatform.Virtual -> ExternalAccess.openUri(Uri("geo:${geohash.latitude},${geohash.longitude}"))
        else -> ExternalAccess.openUri(Uri("https://www.google.com/maps/search/?api=1&query=${geohash.latitude},${geohash.longitude}"))
    }
}