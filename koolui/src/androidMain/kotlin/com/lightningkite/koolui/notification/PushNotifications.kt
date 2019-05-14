package com.lightningkite.koolui.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty

actual object PushNotifications {
    internal var backingToken = StandardObservableProperty<PushNotificationToken?>(null)
    actual val token: ObservableProperty<PushNotificationToken?> get() = backingToken

    actual fun requestNotificationsPermission() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        MyFirebaseMessagingService
    }
}