package com.lightningkite.koolui

import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.notification.Notification
import com.lightningkite.koolui.resources.Resources
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import javafx.application.Platform
import javafx.stage.Stage
import java.awt.Desktop
import java.net.URI
import java.util.*

actual object ApplicationAccess {

    lateinit var stage: Stage

    fun init(classLoader: ClassLoader, stage: Stage) {
        Resources.classLoader = classLoader
        this.stage = stage
        MousePosition.init(stage)
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

    //TODO: Listen
    val isInForegroundPrivate by lazy { StandardObservableProperty<Boolean>(stage.isFocused) }
    actual val isInForeground: ObservableProperty<Boolean> get() = isInForegroundPrivate

    //TODO: run on escape key
    val onBackPressedPrivate = ArrayList<() -> Boolean>()
    actual val onBackPressed: MutableList<() -> Boolean> get() = onBackPressedPrivate

    //TODO: run every 60th of a second
    val onAnimationFramePrivate = ArrayList<() -> Unit>()
    actual val onAnimationFrame: MutableCollection<() -> Unit> get() = onAnimationFramePrivate

    actual fun openUri(uri: Uri) {
        try {
            Desktop.getDesktop().browse(URI.create(uri.string))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO: JavaFX notifications
    actual fun showNotification(notification: Notification) {}

    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    //TODO: Use functions here
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
}
