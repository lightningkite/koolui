package com.lightningkite.koolui.notification

import com.lightningkite.reacktive.property.ObservableProperty

expect object PushNotifications {
    val token: ObservableProperty<PushNotificationToken?>
    fun requestNotificationsPermission()
}