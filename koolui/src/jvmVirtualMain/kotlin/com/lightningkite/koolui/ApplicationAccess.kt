package com.lightningkite.koolui

import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.resources.Resources
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.lightningkite.koolui.notification.Notification
import com.lightningkite.reacktive.invokeAll
import kotlinx.coroutines.Dispatchers

actual object ApplicationAccess {

    actual fun post(action: () -> Unit) {
        GlobalScope.launch(Dispatchers.UI){
            delay(1)
            action()
        }
    }

    fun init(classLoader: ClassLoader) {
        Resources.classLoader = classLoader
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            onException.invokeAll(throwable)
        }
    }

    actual val displaySize: ObservableProperty<Point> = StandardObservableProperty(Point())
    actual val isInForeground: ObservableProperty<Boolean> = StandardObservableProperty(true)
    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList()
    val onAnimationFramePrivate = ArrayList<() -> Unit>()
    actual val onAnimationFrame: MutableCollection<() -> Unit> get() = onAnimationFramePrivate

    actual fun showNotification(notification: Notification) {
        println("show notification $notification")
    }
    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()

    /**
     * Called before the application dies due to an uncaught error.
     * Use this to send an error report.
     */
    actual val onException: MutableList<(throwable: Throwable) -> Unit> = java.util.ArrayList()
}
