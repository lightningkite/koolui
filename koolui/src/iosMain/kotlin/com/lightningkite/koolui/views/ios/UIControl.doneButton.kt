package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.toTimeStamp
import platform.CoreGraphics.CGRect
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*
import platform.darwin.NSObject

fun UIViewAdapter<*>.toolbarWithDoneButton(closureSleeveProvider: (() -> Unit) -> NSObject): UIToolbar {
    val toolbar = UIToolbar(CGRect.zeroVal)
    toolbar.barStyle = UIBarStyleDefault
    toolbar.translucent = true
    toolbar.sizeToFit()
    val sleeve = closureSleeveProvider {
        view.moveToNextOrResign()
    }
    holding["pickerDone"] = sleeve
    val doneButton = UIBarButtonItem(title = "Done", style = UIBarButtonItemStylePlain, target = sleeve, action = NSSelectorFromString("runContainedClosure"))
    toolbar.setItems(listOf(
            UIBarButtonItem(barButtonSystemItem = UIBarButtonSystemItemFlexibleSpace, target = null, action = null),
            doneButton
    ))
    return toolbar
}
