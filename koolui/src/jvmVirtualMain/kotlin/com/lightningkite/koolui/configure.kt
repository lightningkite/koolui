package com.lightningkite.koolui

import com.lightningkite.koolui.resources.Resources
import javafx.stage.Stage

fun configureUi(
        classLoader: ClassLoader,
        stage: Stage
) {
    Resources.classLoader = classLoader
}
