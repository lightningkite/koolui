package com.lightningkite.koolui

import com.lightningkite.lokalize.time.TimeStamp
import platform.Foundation.NSDate

fun NSDate.toTimeStamp(): TimeStamp = TimeStamp(this.timeIntervalSinceReferenceDate.times(1000).toLong())
fun TimeStamp.toNSDate(): NSDate = NSDate(timeIntervalSinceReferenceDate = millisecondsSinceEpoch.toDouble() / 1000.0)
