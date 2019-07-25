package com.lightningkite.koolui

import com.lightningkite.koolui.notification.Notification
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import platform.Foundation.*
import platform.UIKit.*
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
actual object ApplicationAccess {

    fun init() {
        NSSetUncaughtExceptionHandler(staticCFunction { exception: NSException? ->
            dispatch_async(dispatch_get_main_queue()) {
                onException.invokeAll(Exception(
                        message = exception?.let { "${it.name}: ${it.reason}" } ?: "Unknown Exception"
                ))
            }
        })
    }

    var baseVC: UIViewController? = null

    val mutableDisplaySize = StandardObservableProperty<Point>(UIScreen.mainScreen.bounds.useContents { Point(x = size.width.toFloat(), y = size.height.toFloat()) })
    actual val displaySize: ObservableProperty<Point> = mutableDisplaySize

    val mutableIsInForeground = StandardObservableProperty(true)
    actual val isInForeground: ObservableProperty<Boolean> = mutableIsInForeground

    //Never occurs on iOS
    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList()

    actual val onAnimationFrame: MutableCollection<() -> Unit> = ArrayList<() -> Unit>()

    actual fun showNotification(notification: Notification) {
//        val uiNotification = UILocalNotification().apply {
//            fireDate = NSDate.dateWithTimeIntervalSinceNow(0.0)
//            alertTitle = notification.title
//            alertBody = notification.content
//            notification.actions.values.firstOrNull()?.let {
//                this.alertAction
//            }
//            //TODO: Handle priority
//            //TODO: Handle image
//            //TODO: Handle actions
//        }
        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(
                request = UNNotificationRequest.requestWithIdentifier(
                        identifier = notification.id.toString(),
                        content = UNMutableNotificationContent().apply {
                            setTitle(notification.title)
                            setBody(notification.content)
                        },
                        trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)
                ),
                withCompletionHandler = null
        )
    }

    //TODO: Handle action invocations
    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    //TODO: Handle deep link
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()

    actual fun post(action: () -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            action()
        }
    }

    /**
     * Called before the application dies due to an uncaught error.
     * Use this to send an error report.
     */
    actual val onException: MutableList<(throwable: Throwable) -> Unit> = ArrayList()
}
/*
package com.lightningkite.koolui

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.notification.Notification
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.ThreadLocal
import kotlin.native.concurrent.ensureNeverFrozen

@ThreadLocal private val mutableDisplaySize = StandardObservableProperty<Point>(UIScreen.mainScreen.bounds.useContents { Point(x = size.width.toFloat(), y = size.height.toFloat()) })
@ThreadLocal private val mutableIsInForeground = StandardObservableProperty(true)
@ThreadLocal private val mutableOnAnimationFrame: MutableCollection<() -> Unit> = ArrayList<() -> Unit>()
@ThreadLocal private val mutableOnNotificationAction: MutableList<(String) -> Boolean> = ArrayList()
@ThreadLocal private val mutableOnDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
@ThreadLocal private val mutableOnException: MutableList<(throwable: Throwable) -> Unit> = ArrayList()

actual object ApplicationAccess {

    init {
        NSSetUncaughtExceptionHandler(staticCFunction { exception: NSException? ->
            dispatch_async(dispatch_get_main_queue()) {
                mutableOnException.invokeAll(Exception(
                        message = exception?.let{ "${it.name}: ${it.reason}" } ?: "Unknown Exception"
                ))
            }
        })
    }

    val baseVC = AtomicReference<UIViewController?>(null)

    actual val displaySize: ObservableProperty<Point> get() = mutableDisplaySize
    actual val isInForeground: ObservableProperty<Boolean> get() = mutableIsInForeground

    //Never occurs on iOS
    actual val onBackPressed: MutableList<() -> Boolean> get() = ArrayList()

    actual val onAnimationFrame: MutableCollection<() -> Unit> get() = mutableOnAnimationFrame

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
    actual val onNotificationAction: MutableList<(String) -> Boolean> get() = mutableOnNotificationAction

    /**
     * Event for when a deep link is caught by the app.
     * You'll have to register to catch it in the manifests of each platform separately.
     */
    //TODO: Handle deep link
    actual val onDeepLink: MutableList<(url: String) -> Boolean> get() = mutableOnDeepLink

    actual fun post(action: () -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            action()
        }
    }

    /**
     * Called before the application dies due to an uncaught error.
     * Use this to send an error report.
     */
    actual val onException: MutableList<(throwable: Throwable) -> Unit> get() = mutableOnException
}

 */
