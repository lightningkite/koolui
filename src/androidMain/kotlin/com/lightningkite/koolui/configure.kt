package com.lightningkite.koolui

import android.support.annotation.DrawableRes
import com.lightningkite.koolui.android.access.ActivityAccess
import java.io.File

fun configureUi(
    activityAccess: ActivityAccess,
    @DrawableRes notificationIcon: Int
) {
    ApplicationAccess.init(notificationIcon)
    ApplicationAccess.useAccess(activityAccess)
//    Preferences.init(File(activityAccess.context.applicationContext.filesDir, "prefs.json"))
}
