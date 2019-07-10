package com.lightningkite.koolui

import com.lightningkite.koolui.builders.contentRoot
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCMethod
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGSize
import platform.Foundation.NSBundle
import platform.Foundation.NSCoder
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.UIViewControllerTransitionCoordinatorProtocol

class CustomRootController : UIViewController {

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    @OverrideInit
    constructor(nibName: String?, bundle: NSBundle?) : super(nibName, bundle)

    fun <DEPENDENCY : ViewFactory<Layout<*, UIView>>> setup(dependency: DEPENDENCY, viewGenerator: ViewGenerator<DEPENDENCY, Layout<*, UIView>>) {
        view = dependency.contentRoot(viewGenerator).viewAsBase
    }

    override fun viewWillTransitionToSize(size: CValue<CGSize>, withTransitionCoordinator: UIViewControllerTransitionCoordinatorProtocol) {
        super.viewWillTransitionToSize(size, withTransitionCoordinator)
        ApplicationAccess.mutableDisplaySize.value = size.useContents { Point(x = width.toFloat(), y = height.toFloat()) }
    }

    override fun viewDidAppear(animated: Boolean) {
        ApplicationAccess.mutableIsInForeground.value = true
    }

    override fun viewDidDisappear(animated: Boolean) {
        ApplicationAccess.mutableIsInForeground.value = false
    }
}
