package com.lightningkite.koolui

import com.lightningkite.lokalize.time.TimeConstants
import com.lightningkite.lokalize.time.TimeStamp
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

fun NSDate.toTimeStamp(): TimeStamp = TimeStamp(this.timeIntervalSince1970.times(1000).toLong())
fun TimeStamp.toNSDate(): NSDate = NSDate(timeIntervalSinceReferenceDate = (millisecondsSinceEpoch - TimeConstants.MS_PER_DAY * (365 * 31 + 8)).toDouble() / 1000.0)

/*

Leap years in 1970 - 2001
    1972
    1976
    1980
    1984
    1988
    1992
    1996
    2000

 */
