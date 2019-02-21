package com.lightningkite.koolui

import com.lightningkite.recktangle.Point
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGSize
import platform.UIKit.UIViewController
import platform.UIKit.UIViewControllerTransitionCoordinatorProtocol

class CustomRootController : UIViewController() {
    external override fun viewWillTransitionToSize(size: CValue<CGSize>, withTransitionCoordinator: UIViewControllerTransitionCoordinatorProtocol) {
        super.viewWillTransitionToSize(size, withTransitionCoordinator)
        ApplicationAccess.mutableDisplaySize.value = size.useContents { Point(x = width.toFloat(), y = height.toFloat()) }
    }

    external override fun viewDidAppear(animated: Boolean) {
        ApplicationAccess.mutableIsInForeground.value = true
    }

    external override fun viewDidDisappear(animated: Boolean) {
        ApplicationAccess.mutableIsInForeground.value = false
    }
}
