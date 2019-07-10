package com.lightningkite.koolui

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.appendBytes

fun NSData.toByteArray(): ByteArray {
    val data: CPointer<ByteVar> = bytes!!.reinterpret()
    return ByteArray(length.toInt()) { index -> data[index] }
}

fun ByteArray.toNSData(): NSData = NSMutableData().apply {
    if (isEmpty()) return@apply
    this@toNSData.usePinned {
        appendBytes(it.addressOf(0), size.toULong())
    }
}
