package com.lightningkite.koolui.notification

import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty

actual object PushNotifications {
    actual val token: ObservableProperty<PushNotificationToken?> = ConstantObservableProperty(null)
    actual fun requestNotificationsPermission() {
        TODO()
    }
}
