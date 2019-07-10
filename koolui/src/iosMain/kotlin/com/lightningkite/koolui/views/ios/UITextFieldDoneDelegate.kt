package com.lightningkite.koolui.views.ios

import com.lightningkite.koolui.layout.ViewAdapter
import platform.UIKit.*
import platform.darwin.NSObject

class UITextFieldDoneDelegate : NSObject(), UITextFieldDelegateProtocol {
    override fun textFieldShouldReturn(textField: UITextField): Boolean {
        textField.moveToNextOrResign()
        return true
    }
}
