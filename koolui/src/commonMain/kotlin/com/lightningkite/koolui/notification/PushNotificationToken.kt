package com.lightningkite.koolui.notification

import com.lightningkite.koolui.UIPlatform
import com.lightningkite.koolui.current

data class PushNotificationToken(val token: String, val platform: UIPlatform = UIPlatform.current) {
}