package com.lightningkite.koolui.views.dialogs

interface ViewFactoryDialogs<VIEW> {
    /**
     * Launches a dialog with the given view in it.
     */
    fun launchDialog(
            dismissable: Boolean = true,
            onDismiss: () -> Unit = {},
            makeView: (dismissDialog: () -> Unit) -> VIEW
    ): Unit

    /**
     * Launches a selector with options to choose from.
     */
    fun launchSelector(
            title: String? = null,
            options: List<Pair<String, () -> Unit>>
    ): Unit
}