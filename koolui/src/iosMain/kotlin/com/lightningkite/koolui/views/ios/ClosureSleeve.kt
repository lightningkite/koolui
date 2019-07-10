package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.layout.ViewAdapter
import kotlinx.cinterop.*
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*
import platform.darwin.NSObject
import platform.darwin.NSUInteger
import platform.objc.*
import platform.posix.uintVar
import kotlin.native.concurrent.ThreadLocal

class ClosureSleeve(val closure: () -> Unit) : NSObject() {

    @ObjCAction
    fun runContainedClosure() {
        closure()
    }

    @ThreadLocal
    companion object {

        fun fix() {
            val instance = ClosureSleeve {}
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

fun makeSleeve(action: () -> Unit): ClosureSleeve {
    return ClosureSleeve(action)
}

fun <S : UIControl> ViewAdapter<S, UIView>.addAction(events: UIControlEvents, closure: () -> Unit) = addAction(this.view, events, closure)

fun ViewAdapter<*, UIView>.addAction(view: UIControl, events: UIControlEvents, closure: () -> Unit) {
    val sleeve = ClosureSleeve(closure)
//    val pinned = sleeve.pin()
    view.addTarget(sleeve, NSSelectorFromString("runContainedClosure"), events)
    val str = "event_$events"
//    objc_setAssociatedObject(view, str.cstr, sleeve, OBJC_ASSOCIATION_RETAIN_NONATOMIC)
    (this as? UIViewAdapter)?.holding?.set(str, sleeve)
//    (this as? UIViewAdapter)?.holding?.set(str + "_pinned", pinned)
}

fun <S : UIControl, D : Any> ViewAdapter<S, UIView>.setDelegate(type: String, delegate: D): D {
    (this as? UIViewAdapter)?.holding?.set("delegate_$type", delegate)
    return delegate
}

fun ViewAdapter<*, UIView>.addGestureRecognizer(recognizer: UIGestureRecognizer, closure: () -> Unit) {
    val sleeve = ClosureSleeve(closure)
    recognizer.addTarget(sleeve, NSSelectorFromString("runContainedClosure"))
    viewAsBase.addGestureRecognizer(recognizer)
    (this as? UIViewAdapter)?.holding?.set("gestureRecognizer_${recognizer.name}", sleeve)
}
