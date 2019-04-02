package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.layout.ViewAdapter
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.objcPtr
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*
import platform.darwin.NSUInteger
import platform.objc.OBJC_ASSOCIATION_RETAIN
import platform.objc.objc_AssociationPolicy
import platform.objc.objc_setAssociatedObject

class ClosureSleeve(val closure: ()->Unit) {
    fun invoke() = closure()
}

fun <S: UIControl> ViewAdapter<S, UIView>.addAction(events: UIControlEvents, closure: () -> Unit){
    val sleeve = ClosureSleeve(closure)
    view.addTarget(sleeve, NSSelectorFromString("invoke"), events)
    (this as? UIViewAdapter)?.holding?.set("event_$events", sleeve)
}

fun ViewAdapter<*, UIView>.addGestureRecognizer(recognizer: UIGestureRecognizer, closure: () -> Unit){
    val sleeve = ClosureSleeve(closure)
    recognizer.addTarget(sleeve, NSSelectorFromString("invoke"))
    viewAsBase.addGestureRecognizer(recognizer)
    (this as? UIViewAdapter)?.holding?.set("gestureRecognizer_${recognizer.name}", sleeve)
}
