package com.lightningkite.koolui.views.ios

import platform.UIKit.*

private fun UIView.findNextChildFocus(afterIndex: Int = 0): UIView? {
    var index = afterIndex + 1
    while (index < subviews.size) {
        val sub = subviews[index] as UIView
        if (sub is UITextField) {
            return sub
        } else {
            sub.findNextChildFocus()?.let { subFocus ->
                return subFocus
            }
        }
        index += 1
    }
    return null
}

private fun UIView.findNextParentFocus(afterIndex: Int = 0): UIView? {
    findNextChildFocus(afterIndex = afterIndex)?.let { child ->
        return child
    }

    superview?.let { superview ->
        val myIndex = superview.subviews.indexOf(this)
        return superview.findNextParentFocus(afterIndex = myIndex)
    }

    return null
}

fun UIView.findNextFocus(afterIndex: Int = 0): UIView? {

    superview?.let { superview ->
        val myIndex = superview.subviews.indexOf(this)
        return superview.findNextParentFocus(afterIndex = myIndex)
    }

    return null
}


fun UIResponder.moveToNextOrResign() {
    (this as? UIView)?.findNextFocus()?.let { next ->
        next.becomeFirstResponder()
    } ?: run {
        this.resignFirstResponder()
    }
}
