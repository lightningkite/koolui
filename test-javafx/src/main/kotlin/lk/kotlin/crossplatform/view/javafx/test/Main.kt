package lk.kotlin.crossplatform.view.javafx.test

import com.lightningkite.kommunicate.HttpClient
import com.lightningkite.koolui.color.Theme
import com.lightningkite.koolui.configureUi
import com.lightningkite.koolui.test.MainVG
import com.lightningkite.koolui.views.MaterialJavaFxViewFactory
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {

    val view = MainVG<Node>()

    override fun start(primaryStage: Stage) {
        configureUi(Main::class.java.classLoader, primaryStage)
        val view = with(MaterialJavaFxViewFactory(Theme.dark(), resourceFetcher = { javaClass.getResourceAsStream(it) }, scale = 1.0)) {
            val v: Node = view.generate(this)
            v.lifecycle.alwaysOn = true
            v
        }
        primaryStage.scene = Scene(view as Parent)
        primaryStage.show()
    }
}

fun main(vararg args: String) {
    Application.launch(Main::class.java)
}
