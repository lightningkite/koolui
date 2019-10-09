package com.lightningkite.koolui.views

import com.lightningkite.koolui.color.ColorSet
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.views.basic.LayoutJavaFxBasic
import com.lightningkite.koolui.views.graphics.LayoutJavaFxGraphics
import com.lightningkite.koolui.views.interactive.LayoutJavaFxInteractive
import com.lightningkite.koolui.views.layout.LayoutJavaFxLayout
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Rectangle
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane

/**
 * This is an example how how to set up your factory.
 * It is not recommended you use this directly - instead copy it and use the modules you want.
 */
class LayoutJavaFxViewFactory(
        theme: Theme,
        colorSet: ColorSet = theme.main,
        override val scale: Double = 1.0
) : ViewFactory<Layout<*, Node>>,
        HasScale,
        Themed by Themed.impl(theme, colorSet),
        LayoutJavaFxBasic /*ViewFactoryBasic*/,
        LayoutJavaFxInteractive /*ViewFactoryInteractive*/,
        LayoutJavaFxGraphics /*ViewFactoryGraphics*/,
        LayoutJavaFxLayout /*ViewFactoryLayout*/,
        ViewFactoryNavigationDefault<Layout<*, Node>> /*ViewFactoryNavigation*/,
        LayoutVFRootAndDialogs<Node> /*ViewFactoryDialogs*/,
        JavaFxLayoutWrapper /*ViewLayoutWrapper*/ {

    override var root: Layout<*, Node>? = null
}