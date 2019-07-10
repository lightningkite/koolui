package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.toTimeStamp
import platform.CoreGraphics.CGRect
import platform.Foundation.NSSelectorFromString
import platform.UIKit.*

fun UIViewAdapter<*>.toolbarWithDoneButton(): UIToolbar {
    val toolbar = UIToolbar(CGRect.zeroVal)
    toolbar.barStyle = UIBarStyleDefault
    toolbar.translucent = true
    toolbar.sizeToFit()
//    val sleeve = ClosureSleeve {
//        view.moveToNextOrResign()
//    }
//    holding["pickerDone"] = sleeve
//    val doneButton = UIBarButtonItem(title = "Done", style = UIBarButtonItemStylePlain, target = sleeve, action = sleeve.selector)
    toolbar.setItems(listOf(
//            doneButton,
            UIBarButtonItem(barButtonSystemItem = UIBarButtonSystemItemFlexibleSpace, target = null, action = null)
    ))
    return toolbar
}
