package com.lightningkite.koolui.test

import kotlinx.cinterop.*
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.objc.*
import kotlin.native.concurrent.ThreadLocal

fun makeMainVC(): UIViewController = MainUIViewController(null, null)
