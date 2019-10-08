package com.lightningkite.koolui

import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.notification.Notification
import com.lightningkite.koolui.resources.Resources
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import javafx.animation.AnimationTimer
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.Desktop
import java.net.URI
import java.util.*

actual object ApplicationAccess {

    lateinit var stage: Stage

    fun init(classLoader: ClassLoader, stage: Stage) {
        Resources.classLoader = classLoader
        this.stage = stage
        MousePosition.init(stage)
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            onException.invokeAll(throwable)
        }
        stage.widthProperty().addListener { _, _, it ->
            displaySizePrivate.value = displaySizePrivate.value.copy(x = it.toFloat())
        }
        stage.heightProperty().addListener { _, _, it ->
            displaySizePrivate.value = displaySizePrivate.value.copy(y = it.toFloat())
        }
    }

    init {
        object : AnimationTimer() {
            override fun handle(now: Long) {
                onAnimationFramePrivate.invokeAll()
            }
        }.start()
    }

    //TODO: Listen
    val displaySizePrivate by lazy {
        StandardObservableProperty<Point>(
            Point(
                stage.width.toFloat(),
                stage.height.toFloat()
            )
        )
    }
    actual val displaySize: ObservableProperty<Point> get() = displaySizePrivate

    actual fun post(action: () -> Unit) {
        Platform.runLater(action)
    }

    //TODO: Listen
    val isInForegroundPrivate by lazy { StandardObservableProperty<Boolean>(stage.isFocused) }
    actual val isInForeground: ObservableProperty<Boolean> get() = isInForegroundPrivate

    //TODO: run on escape key
    val onBackPressedPrivate = ArrayList<() -> Boolean>()
    actual val onBackPressed: MutableList<() -> Boolean> get() = onBackPressedPrivate

    val onAnimationFramePrivate = ArrayList<() -> Unit>()
    actual val onAnimationFrame: MutableCollection<() -> Unit> get() = onAnimationFramePrivate

    //TODO: JavaFX notifications
    actual fun showNotification(notification: Notification) {}

    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    //TODO: Use functions here
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()

    /**
     * Called before the application dies due to an uncaught error.
     * Use this to send an error report.
     */
    actual val onException: MutableList<(throwable: Throwable) -> Unit> = ArrayList()
}
