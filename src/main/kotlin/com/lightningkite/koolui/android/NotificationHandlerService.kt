package com.lightningkite.koolui.android

import android.app.IntentService
import android.content.Intent
import com.lightningkite.koolui.ApplicationAccess
import java.util.*

class NotificationHandlerService : IntentService("Notification Handler Service") {

    companion object {
        const val EXTRA_ACTION = "action"
    }

    override fun onHandleIntent(intent: Intent) {
        val action = intent.getStringExtra(EXTRA_ACTION) ?: return
        ApplicationAccess.onNotificationAction.asReversed().firstOrNull { it.invoke(action) }
    }


}
