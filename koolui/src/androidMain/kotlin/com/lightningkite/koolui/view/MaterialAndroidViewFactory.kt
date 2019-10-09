package com.lightningkite.koolui.view

import android.view.View
import com.lightningkite.koolui.android.access.ActivityAccess
import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.view.basic.LayoutAndroidBasic
import com.lightningkite.koolui.view.graphics.LayoutAndroidGraphics
import com.lightningkite.koolui.view.graphics.LayoutAndroidInteractive
import com.lightningkite.koolui.view.layout.LayoutAndroidLayout
import com.lightningkite.koolui.view.navigation.LayoutAndroidNavigation
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.ViewFactory

/**
 * This is an example how how to set up your factory.
 * It is not recommended you use this directly - instead copy it and use the modules you want.
 */
class MaterialAndroidViewFactory(
        override val activityAccess: ActivityAccess,
        theme: Theme,
        colorSet: ColorSet = theme.main
) : ViewFactory<Layout<*, View>>,
        Themed by Themed.impl(theme, colorSet),
        LayoutAndroidBasic /*ViewFactoryBasic*/,
        LayoutAndroidInteractive /*ViewFactoryInteractive*/,
        LayoutAndroidGraphics /*ViewFactoryGraphics*/,
        LayoutAndroidLayout /*ViewFactoryLayout*/,
        LayoutAndroidNavigation /*ViewFactoryNavigation*/,
        LayoutVFRootAndDialogs<View> /*ViewFactoryDialogs*/,
        LayoutAndroidWrapper /*LayoutViewWrapper*/ {
    override var root: Layout<*, View>? = null
}

