package com.lightningkite.koolui.test

import kotlinx.cinterop.ObjCAction
import platform.darwin.NSObject

class MyClosureSleeve(val closure: () -> Unit) : NSObject() {
    @ObjCAction
    fun runContainedClosure() = closure()
}
