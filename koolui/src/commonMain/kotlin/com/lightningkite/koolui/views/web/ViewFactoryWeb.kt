package com.lightningkite.koolui.views.web

import com.lightningkite.reacktive.property.ObservableProperty

interface ViewFactoryWeb<VIEW> {

    /**
     * Shows a webpage within this view.
     * If the string starts with 'http://', it will be interpreted as a URL.
     * Otherwise, it will be interpreted as non-interactive HTML content.
     */
    fun web(
            content: ObservableProperty<String>
    ): VIEW
}