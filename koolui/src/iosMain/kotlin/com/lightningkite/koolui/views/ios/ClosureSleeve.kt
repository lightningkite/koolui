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

fun <S : UIControl> ViewAdapter<S, UIView>.addAction(events: UIControlEvents, sleeve: NSObject) = addAction(this.view, events, sleeve)

fun ViewAdapter<*, UIView>.addAction(view: UIControl, events: UIControlEvents, sleeve: NSObject) {
//    val pinned = sleeve.pin()
    view.addTarget(sleeve, NSSelectorFromString("runContainedClosure"), events)
    (this as? UIViewAdapter)?.holding?.set("event_$events", sleeve)
}

fun <S : UIControl, D : Any> ViewAdapter<S, UIView>.setDelegate(type: String, delegate: D): D {
    (this as? UIViewAdapter)?.holding?.set("delegate_$type", delegate)
    return delegate
}

fun ViewAdapter<*, UIView>.addGestureRecognizer(recognizer: UIGestureRecognizer, sleeve: NSObject) {
    recognizer.addTarget(sleeve, NSSelectorFromString("runContainedClosure"))
    viewAsBase.addGestureRecognizer(recognizer)
    (this as? UIViewAdapter)?.holding?.set("gestureRecognizer_${recognizer.name}", sleeve)
}
