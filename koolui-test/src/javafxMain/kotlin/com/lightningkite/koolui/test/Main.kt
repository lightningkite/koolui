package com.lightningkite.koolui.test

import com.lightningkite.koolui.color.*
import com.lightningkite.koolui.views.*
import com.lightningkite.koolui.builders.*
import com.lightningkite.koolui.*
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {

    companion object {
        val mainVg = MainVG<Node>()
    }

    class Factory(colorSet: ColorSet = theme.main) : MyViewFactory<Node>, ViewFactory<Node> by MaterialJavaFxViewFactory(theme, colorSet, scale = 1.0) {

        override fun withColorSet(colorSet: ColorSet): ViewFactory<Node> = Factory(colorSet)
    }

    override fun start(primaryStage: Stage) {
        ApplicationAccess.init(Main::class.java.classLoader, primaryStage)
        primaryStage.scene = Scene(Factory().contentRoot(mainVg) as Parent)
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}
