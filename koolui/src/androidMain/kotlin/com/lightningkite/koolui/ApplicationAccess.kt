package com.lightningkite.koolui

import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat
import com.lightningkite.koolui.android.NotificationHandlerService
import com.lightningkite.koolui.android.access.ActivityAccess
import com.lightningkite.koolui.notification.Notification
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.recktangle.Point
import kotlin.math.roundToInt

actual object ApplicationAccess {

    fun init(
            activityAccess: ActivityAccess,
            @DrawableRes notificationIcon: Int
    ) {
        ApplicationAccess.init(notificationIcon)
        ApplicationAccess.useAccess(activityAccess)
//    Preferences.init(File(activityAccess.context.applicationContext.filesDir, "prefs.json"))
    }


    var access: ActivityAccess? = null
    var notificationIcon: Int = 0

    val pauseListener = { ->
        privateIsInForeground.value = false
    }
    val resumeListener = { ->
        privateIsInForeground.value = true
    }
    val backListener = { ->
        onBackPressed.reversed().any { it.invoke() }
    }

    var dip: Float = 1f

    private val privateDisplaySize = StandardObservableProperty(Point())
    actual val displaySize: ObservableProperty<Point> //can change on rotation, etc.
        get() = privateDisplaySize

    private val privateIsInForeground = StandardObservableProperty(false)
    actual val isInForeground: ObservableProperty<Boolean> get() = privateIsInForeground

    actual val onBackPressed: MutableList<() -> Boolean> = ArrayList<() -> Boolean>()

    actual val onAnimationFrame: MutableCollection<() -> Unit> = ArrayList()

    val defaultChannelId = "default_channel"
    var channelCreated = false

    actual fun showNotification(notification: Notification) {
        val context = access!!.context
        NotificationCompat.Builder(context, defaultChannelId)
            .setSmallIcon(notificationIcon)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setPriority(notification.priority.times(4).minus(2).roundToInt())
            .setAutoCancel(true)
            .let {
                val pending = getNotificationPendingIntent(context, notification.action)
                it.setContentIntent(pending)
            }
            .let {
                for (action in notification.actions) {
                    val pending = getNotificationPendingIntent(context, action.value)
                    it.addAction(0, action.key, pending)
                }
                it
            }
            .build()
            .let {
                ApplicationAccess.access!!.context.getSystemService(Context.NOTIFICATION_SERVICE)
                    .let { it as NotificationManager }
                    .notify(notification.id, it)
            }
    }

    fun getNotificationPendingIntent(context: Context, action: String): PendingIntent? {
        val intent = Intent(context, NotificationHandlerService::class.java)
        intent.putExtra(NotificationHandlerService.EXTRA_ACTION, action)
        return PendingIntent.getService(context, action.hashCode(), intent, 0)
    }

    actual val onNotificationAction: MutableList<(String) -> Boolean> = ArrayList()

    init {
        ValueAnimator().apply {
            setIntValues(0, 100)
            duration = 10000L
            repeatCount = ValueAnimator.INFINITE
            repeatCount = ValueAnimator.RESTART
            addUpdateListener { onAnimationFrame.invokeAll() }
            start()
        }
    }

    fun init(
        @DrawableRes notificationIcon: Int
    ) {
        this.notificationIcon = notificationIcon
    }

    fun useAccess(access: ActivityAccess) {
        val oldAccess = this.access
        oldAccess?.onResume?.remove(resumeListener)
        oldAccess?.onPause?.remove(pauseListener)
        oldAccess?.onBackPressed?.remove(backListener)

        dip = access.context.resources.displayMetrics.density

        this.access = access
        privateDisplaySize.value = access.context.resources.displayMetrics.let {
            Point(
                it.widthPixels / it.density,
                it.heightPixels / it.density
            )
        }
        privateIsInForeground.value = true
        access.onPause.add(pauseListener)
        access.onResume.add(resumeListener)
        access.onBackPressed.add(backListener)

        if (!channelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelCreated = true
            ApplicationAccess.access!!.context.getSystemService(Context.NOTIFICATION_SERVICE)
                .let { it as NotificationManager }
                .createNotificationChannel(
                    NotificationChannel(defaultChannelId, "Default", NotificationManager.IMPORTANCE_DEFAULT)
                )
        }
    }

    //TODO - use the functions here
    actual val onDeepLink: MutableList<(url: String) -> Boolean> = ArrayList()
}
