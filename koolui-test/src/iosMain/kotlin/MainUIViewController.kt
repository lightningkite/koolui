package com.lightningkite.koolui.test

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.builders.contentRoot
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.koolui.views.ios.ClosureSleeve
import com.lightningkite.koolui.views.ios.LayoutRootView
import com.lightningkite.koolui.views.ios.UIKitViewFactory
import com.lightningkite.koolui.views.ios.zeroVal
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGSize
import platform.Foundation.NSBundle
import platform.Foundation.NSCoder
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.UIViewControllerTransitionCoordinatorProtocol
import platform.UIKit.layoutSubviews

typealias LUIV = Layout<*, UIView>

class MainUIViewController : UIViewController {

    class Factory(
            theme: Theme,
            colorSet: ColorSet,
            val basedOn: ViewFactory<LUIV> = UIKitViewFactory(::MyClosureSleeve, theme, colorSet)
    ) : MyViewFactory<LUIV>, ViewFactory<LUIV> by basedOn {
        override fun withColorSet(colorSet: ColorSet) = Factory(theme, colorSet, basedOn.withColorSet(colorSet))
    }

    @OverrideInit
    constructor(coder: NSCoder) : super(coder) {
        commonInit()
    }

    @OverrideInit
    constructor(nibName: String?, bundle: NSBundle?) : super(nibName, bundle) {
        commonInit()
    }

    val main = MainVG<Layout<*, UIView>>()

    fun commonInit() {
        view = LayoutRootView(CGRect.zeroVal).apply {
            setup(Factory(theme, theme.main).contentRoot(main))
        }
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

    override fun viewWillLayoutSubviews() {
        (view as LayoutRootView).layoutSubviews()
    }
}

/*package com.lightningkite.koolui.test

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.builders.contentRoot
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.koolui.views.ios.LayoutRootView
import com.lightningkite.koolui.views.ios.UIKitViewFactory
import com.lightningkite.koolui.views.ios.zeroVal
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.Foundation.*
import platform.UIKit.*

class MainUIViewController : UIViewController {

    class Factory(theme: Theme, colorSet: ColorSet) : MyViewFactory<Layout<*, UIView>>, ViewFactory<Layout<*, UIView>> by UIKitViewFactory(theme, colorSet) {
        override fun withColorSet(colorSet: ColorSet): ViewFactory<Layout<*, UIView>> = Factory(theme, colorSet)
    }

    @OverrideInit
    constructor(coder: NSCoder) : super(coder) {
        commonInit()
    }

    @OverrideInit
    constructor(nibName: String?, bundle: NSBundle?) : super(nibName, bundle) {
        commonInit()
    }

    val main = MainVG<Layout<*, UIView>>()
    lateinit var mainView: UIView

    fun commonInit() {
        mainView = LayoutRootView(CGRect.zeroVal).apply {
            setup(Factory(theme, theme.main).contentRoot(main))
        }
        view.addSubview(mainView)
        NSNotificationCenter.defaultCenter.addObserver(this, NSSelectorFromString("onShowKeyboard"), UIKeyboardWillShowNotification, null)
        NSNotificationCenter.defaultCenter.addObserver(this, NSSelectorFromString("onHideKeyboard"), UIKeyboardWillHideNotification, null)
    }

    @ObjCAction fun onShowKeyboard(sender: NSNotification){
        println("onShowKeyboard")
        val keyboardHeight = (sender.userInfo!![UIKeyboardFrameEndUserInfoKey] as NSValue).CGRectValue().useContents { size.height }
        val width = view.frame.useContents { size.width }
        val height = view.frame.useContents { size.height }
        mainView.setFrame(CGRectMake(
                x = 0.0,
                y = 0.0,
                width = width,
                height = height - keyboardHeight
        ))
    }

    @ObjCAction fun onHideKeyboard(sender: NSNotification){
        println("onHideKeyboard")
        val width = view.frame.useContents { size.width }
        val height = view.frame.useContents { size.height }
        mainView.setFrame(CGRectMake(
                x = 0.0,
                y = 0.0,
                width = width,
                height = height
        ))
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

    override fun viewWillLayoutSubviews() {
        val width = view.frame.useContents { size.width }
        val height = view.frame.useContents { size.height }
        mainView.setFrame(CGRectMake(
                x = 0.0,
                y = 0.0,
                width = width,
                height = height
        ))
        (mainView as LayoutRootView).layoutSubviews()
    }


}
*/
