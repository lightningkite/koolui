package com.lightningkite.koolui

import com.lightningkite.kommon.collection.WeakHashMap
import com.lightningkite.recktangle.Point
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import java.beans.EventHandler

object MousePosition {
    var stages = WeakHashMap<Stage, Stage>()
    val point = Point()
    fun init(stage: Stage) {
        if (stages.containsKey(stage)) return
        stages[stage] = stage
        stage.addEventFilter(MouseEvent.MOUSE_MOVED) {
            stage.scene?.window?.let { window ->
                point.x = it.screenX.toFloat()
                point.y = it.screenY.toFloat()
            }
        }
    }
}