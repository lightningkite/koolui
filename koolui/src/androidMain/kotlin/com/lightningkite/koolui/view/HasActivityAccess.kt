package com.lightningkite.koolui.view

import android.content.Context
import com.lightningkite.koolui.android.access.ActivityAccess

interface HasActivityAccess {
    val activityAccess: ActivityAccess
    val context: Context get() = activityAccess.context
}