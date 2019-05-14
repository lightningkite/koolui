package com.lightningkite.koolui.notification

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lightningkite.koolui.ApplicationAccess

class MyFirebaseMessagingService(): FirebaseMessagingService() {
    companion object {
        init {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                if(it.isSuccessful){
                    PushNotifications.backingToken.value = it.result?.token?.let{ PushNotificationToken(it) }
                }
            }
        }
    }

    override fun onNewToken(p0: String?) {
        PushNotifications.backingToken.value = p0?.let{ PushNotificationToken(it) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val notification = Notification(
                id = data["n_id"]?.toIntOrNull() ?: 0,
                priority = data["n_priority"]?.toFloatOrNull() ?: .5f,
                title = data["n_title"] ?: "",
                content = data["n_content"] ?: "",
                image = data["n_image"],
                action = data["n_action"] ?: "",
                actions = data["n_actions"]?.let {
                    it.split('|').associate {
                        it.substringBefore('=') to it.substringAfter('=')
                    }
                } ?: mapOf()
        )
        ApplicationAccess.showNotification(notification)
    }
}