package com.lightningkite.koolui.android.dialog

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.lightningkite.koolui.android.access.ActivityAccess

fun Context.dialog(
    dismissible: Boolean = true,
    windowModifier: Window.() -> Unit = {},
    layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {},
    theme: Int? = null,
    viewGenerator: (ActivityAccess) -> View
) {
    val id: Int = viewGenerator.hashCode()
    GenericDialogActivity.containers[id] =
            GenericDialogActivity.ContainerData(viewGenerator, layoutParamModifier, windowModifier, theme)
    startActivity(Intent(this, GenericDialogActivity::class.java).apply {
        putExtra(GenericDialogActivity.EXTRA_CONTAINER, id)
        putExtra(GenericDialogActivity.EXTRA_DISMISS_ON_TOUCH_OUTSIDE, dismissible)
    })
}
