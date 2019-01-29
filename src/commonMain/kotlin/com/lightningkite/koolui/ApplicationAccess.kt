package com.lightningkite.koolui

import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.recktangle.Point

expect object ApplicationAccess {
    val displaySize: ObservableProperty<Point> //can change on rotation, etc.
    val isInForeground: ObservableProperty<Boolean>
    /**
     * Called in reverse order until one of the lambdas returns true, indicating the press has been handled.
     */
    val onBackPressed: MutableList<() -> Boolean>
    val onAnimationFrame: MutableCollection<() -> Unit>

    fun openUri(uri: String)

    fun showNotification(notification: Notification)
    /**
     * Event for when a notification action is fired.
     * Called in reverse order until one of the lambdas returns true, indicating the action has been handled.
     * Null action means default/view.
     */
    val onNotificationAction: MutableList<(action: String) -> Boolean>

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    val onDeepLink: MutableList<(url: String) -> Boolean>
}
