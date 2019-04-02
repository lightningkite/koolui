package com.lightningkite.koolui.views.ios

import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero

val CGRect.Companion.zeroVal get() = CGRectZero.readValue()
