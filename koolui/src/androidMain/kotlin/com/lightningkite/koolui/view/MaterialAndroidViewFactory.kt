package com.lightningkite.koolui.view

import android.view.View
import android.view.ViewGroup
import com.lightningkite.koolui.android.*
import com.lightningkite.koolui.android.access.ActivityAccess
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.view.basic.LayoutAndroidBasic
import com.lightningkite.koolui.view.graphics.LayoutAndroidGraphics
import com.lightningkite.koolui.view.graphics.LayoutAndroidInteractive
import com.lightningkite.koolui.view.layout.LayoutAndroidLayout
import com.lightningkite.koolui.view.navigation.LayoutAndroidNavigation
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Rectangle

class MaterialAndroidViewFactory(
        override val activityAccess: ActivityAccess,
        theme: Theme,
        colorSet: ColorSet = theme.main
) : ViewFactory<Layout<*, View>>,
        Themed by Themed.impl(theme, colorSet),
        LayoutAndroidBasic,
        LayoutAndroidInteractive,
        LayoutAndroidGraphics,
        LayoutAndroidLayout,
        LayoutAndroidNavigation,
        LayoutVFRootAndDialogs<View> {

    override fun intrinsicDimensionLayouts(view: View): LeafDimensionLayouts = IntrinsicDimensionLayouts(view)

    override var root: Layout<*, View>? = null

    override fun applyEntranceTransition(view: View, animation: Animation) {
        val parent = view.parent as? ViewGroup ?: return
        animation.android().animateIn.invoke(view, parent)
    }

    override fun applyExitTransition(view: View, animation: Animation, onComplete: () -> Unit) {
        val parent = view.parent as? ViewGroup ?: run {
            onComplete()
            return
        }
        animation.android().animateOut.invoke(view, parent).withEndAction(onComplete)
    }

    override fun defaultViewContainer(): View = ManualLayout(context)

    override fun <SPECIFIC : View> SPECIFIC.adapter() = AndroidLayoutAdapter(this, true)


    override fun nativeViewAdapter(wraps: Layout<*, View>): View = LayoutToAndroidView(context).apply {
        this.layout = wraps
    }


}