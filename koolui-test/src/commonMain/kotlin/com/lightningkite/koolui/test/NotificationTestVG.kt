package com.lightningkite.koolui.test

import com.lightningkite.lokalize.*
import com.lightningkite.lokalize.time.*
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.Notification
import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class NotificationTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Notifications"

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
