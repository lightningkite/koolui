package com.lightningkite.koolui.test

import com.lightningkite.koolui.color.*
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.*
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.forceHeight
import com.lightningkite.koolui.views.basic.text
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

    class Factory(val basedOn: LayoutJavaFxViewFactory = LayoutJavaFxViewFactory(theme)) : MyViewFactory<Layout<*, Node>>, ViewFactory<Layout<*, Node>> by basedOn

    override fun start(primaryStage: Stage) {
        ApplicationAccess.init(Main::class.java.classLoader, primaryStage)
        val root = with(Factory()) { basedOn.nativeViewAdapter(contentRoot(mainVg)) }
//        val root = with(Factory()) { basedOn.nativeViewAdapter(scrollVertical(text("asdf").setHeight(800f))) }
        primaryStage.scene = Scene(root)
//        GlobalScope.launch(Dispatchers.UI) {
//            while (true) {
//                delay(3000)
//                fun Node.asdf(spaces: Int = 0) {
//                    println("${" ".repeat(spaces)}${this::class.java.simpleName} - ${(this as? Region)?.layoutX} ${(this as? Region)?.layoutY} ${(this as? Region)?.width} ${(this as? Region)?.height}")
//                    if (this is Label) {
//                        println("${" ".repeat(spaces)} Label text: ${this.text}")
//                    }
//                    if (this is Parent) {
//                        for (child in this.childrenUnmodifiable) {
//                            child.asdf(spaces = spaces + 1)
//                        }
//                    }
//                }
//                root.asdf()
//            }
//        }
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}
