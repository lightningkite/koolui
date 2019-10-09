package com.lightningkite.koolui.test

import com.lightningkite.koolui.color.*
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.*
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.forceHeight
import com.lightningkite.koolui.layout.views.LayoutVFRootAndDialogs
import com.lightningkite.koolui.views.basic.LayoutJavaFxBasic
import com.lightningkite.koolui.views.basic.text
import com.lightningkite.koolui.views.graphics.LayoutJavaFxGraphics
import com.lightningkite.koolui.views.interactive.LayoutJavaFxInteractive
import com.lightningkite.koolui.views.layout.LayoutJavaFxLayout
import com.lightningkite.koolui.views.navigation.ViewFactoryNavigationDefault
import com.lightningkite.koolui.views.root.contentRoot
import javafx.application.Application
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Main : Application() {

    companion object {
        val mainVg = MainVG<Layout<*, Node>>()
    }

    //Here, you pick out what GUI modules you want to use
    class Factory(
            theme: Theme = myTheme,
            colorSet: ColorSet = theme.main,
            override val scale: Double = 1.0
    ) : MyViewFactory<Layout<*, Node>>,
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

    override fun start(primaryStage: Stage) {
        ApplicationAccess.init(Main::class.java.classLoader, primaryStage)
        val root = with(Factory()) { nativeViewAdapter(contentRoot(mainVg)) }
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}
