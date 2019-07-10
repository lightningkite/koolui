package com.lightningkite.koolui.test

import com.lightningkite.koolui.views.ios.ClosureSleeve
import kotlinx.cinterop.*
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.objc.*
import kotlin.native.concurrent.ThreadLocal

fun makeMainVC(): UIViewController = MainUIViewController(null, null)
fun setup() {
    ClosureSleeve.fix()
    NearerClosureSleeve.fix()
}

fun makeNearerClosureSleeve(closure: () -> Unit) = NearerClosureSleeve(closure)

class NearerClosureSleeve(val closure: () -> Unit) : NSObject() {

    @ObjCAction
    fun runContainedClosure() {
        closure()
    }

    @ThreadLocal
    companion object {

        fun fix() {
            val instance = NearerClosureSleeve {}
            val type = object_getClass(instance)

            println("Now there are implementations:")
            memScoped {
                val sizeOutput = this.alloc<UIntVar>()
                println("My class: ${type}")
                val mlist = class_copyMethodList(type, sizeOutput.ptr)
                if (mlist == null) {
                    println("Failed to pull selectors, null response.")
                    return@memScoped
                }
                val size = sizeOutput.value.toInt()
                println("Selector size: $size")
                for (i in 0 until size) {
                    println("Method $i: ${sel_getName(method_getName(mlist[i]))?.toKString()}")
                }
            }
            println("End selectors.")
        }
    }
}
