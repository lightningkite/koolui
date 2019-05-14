package com.lightningkite.koolui

import com.lightningkite.koolui.notification.Notification
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.useContents
import platform.Foundation.NSDate
import platform.Foundation.NSURL
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.UIKit.UIApplication
import platform.UIKit.UILocalNotification
import platform.UIKit.UIScreen
import platform.UIKit.scheduleLocalNotification

@ThreadLocal
actual object ApplicationAccess {

    val mutableDisplaySize = StandardObservableProperty<Point>(UIScreen.mainScreen.bounds.useContents { Point(x = size.width.toFloat(), y = size.height.toFloat()) })
    actual val displaySize: ObservableProperty<Point> = mutableDisplaySize

    val mutableIsInForeground = StandardObservableProperty(true)
    actual val isInForeground: ObservableProperty<Boolean> = mutableIsInForeground

    //Never occurs on iOS
    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList()

    actual val onAnimationFrame: MutableCollection<() -> Unit> = ArrayList<() -> Unit>()

    actual fun openUri(uri: String) {
        println("Opening URI $uri")
        val url = NSURL.URLWithString(uri)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        } else {
            //TODO: Alert the user?
        }
    }

    actual fun showNotification(notification: Notification) {
        UIApplication.sharedApplication.scheduleLocalNotification(UILocalNotification().apply {
            fireDate = NSDate.dateWithTimeIntervalSinceNow(0.0)
            alertTitle = notification.title
            alertBody = notification.content
            //TODO: Handle priority
            //TODO: Handle image
            //TODO: Handle actions
        })
    }

    //TODO: Handle action invocations
    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    //TODO: Handle deep link
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
}
