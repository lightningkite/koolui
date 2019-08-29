package com.lightningkite.koolui.views.dialogs

import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.views.interactive.ViewFactoryInteractive
import com.lightningkite.koolui.views.layout.ViewFactoryLayout
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.button
import com.lightningkite.koolui.views.layout.*


fun <X, VIEW> X.launchConfirmationDialog(
        title: String = "",
        message: String = "Are you sure you want to do this?",
        onCancel: () -> Unit = {},
        onConfirm: () -> Unit
) where X : ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW>, X: ViewFactoryDialogs<VIEW>, X: ViewFactoryInteractive<VIEW> {
    var approved = false
    launchDialog(
            dismissable = true,
            onDismiss = {
                if (approved)
                    onConfirm.invoke()
                else
                    onCancel.invoke()
            }
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "Cancel") {
                    approved = false
                    dismiss()
                }
                -button(label = "OK") {
                    approved = true
                    dismiss()
                }
            }
        }))
    }
}

fun <X, VIEW> X.launchInfoDialog(
        title: String,
        message: String,
        onDismiss: () -> Unit = {}
) where X : ViewFactoryBasic<VIEW>, X: ViewFactoryLayout<VIEW>, X: ViewFactoryDialogs<VIEW>, X: ViewFactoryInteractive<VIEW> {
    launchDialog(
            dismissable = true,
            onDismiss = onDismiss
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "OK") {
                    dismiss()
                }
            }
        }).margin(8f))
    }
}
