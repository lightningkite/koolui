package com.lightningkite.koolui

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point
import kotlin.browser.window

actual object ApplicationAccess {

    lateinit var appPath: String

    //TODO: Handle dynamic display size changes
    actual val displaySize: ObservableProperty<Point>
        get() = ConstantObservableProperty(Point(window.innerWidth.toFloat(), window.innerHeight.toFloat()))
    actual val isInForeground: ObservableProperty<Boolean> = ConstantObservableProperty(true)

    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList()
    actual val onAnimationFrame: MutableCollection<() -> Unit> by lazy {
        val list = ArrayList<() -> Unit>()
        window.setInterval({ list.invokeAll() }, 30)
        list
    }

    actual fun openUri(uri: String) {
        window.location.href = uri
    }

    actual fun runLater(action: () -> Unit) {
        window.setTimeout(action, 1)
    }

    actual fun runAfterDelay(delayMilliseconds: Long, action: () -> Unit) {
        window.setTimeout(action, delayMilliseconds.toInt())
    }

    //TODO: Web notifications
    actual fun showNotification(notification: Notification) {}

    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    //TODO - use the functions here
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
}
