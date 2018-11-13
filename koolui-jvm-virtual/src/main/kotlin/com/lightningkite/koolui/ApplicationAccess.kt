package com.lightningkite.koolui

import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual object ApplicationAccess {
    actual val displaySize: ObservableProperty<Point> = StandardObservableProperty(Point())
    actual val isInForeground: ObservableProperty<Boolean> = StandardObservableProperty(true)
    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList()
    val onAnimationFramePrivate = ArrayList<() -> Unit>()
    actual val onAnimationFrame: MutableCollection<() -> Unit> get() = onAnimationFramePrivate

    actual fun openUri(uri: String){
        println("Opening URI $uri")
    }

    actual fun runLater(action: () -> Unit) {
        GlobalScope.launch { action() }
    }

    actual fun runAfterDelay(delayMilliseconds: Long, action: () -> Unit) {
        GlobalScope.launch {
            delay(delayMilliseconds)
            action()
        }
    }

    actual fun showNotification(notification: Notification) {
        println("show notification $notification")
    }
    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
}
