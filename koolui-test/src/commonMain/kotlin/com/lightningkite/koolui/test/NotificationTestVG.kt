package com.lightningkite.koolui.test

import com.lightningkite.lokalize.*
import com.lightningkite.lokalize.time.*
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*
import com.lightningkite.koolui.notification.Notification
import kotlin.native.concurrent.ThreadLocal

class NotificationTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Notifications"

    @ThreadLocal
    companion object {
        val lastActionObs = StandardObservableProperty("No actions taken yet.")

        init {
            ApplicationAccess.onNotificationAction += {
                lastActionObs.value = "Action '$it' taken at ${Locale.default.renderTimeStamp(TimeStamp.now())}"
                false
            }
        }
    }

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "Press below to fire a notification test notification off.")
            -button(label = "Get Notified!") {
                ApplicationAccess.showNotification(Notification(
                        title = "Test Notification",
                        content = "This is a test notification.",
                        priority = .5f,
                        action = "view",
                        actions = mapOf(
                                "Silence" to "silence",
                                "Yell" to "yell"
                        )
                ))
            }
            -text(text = lastActionObs)
        }
    }
}
