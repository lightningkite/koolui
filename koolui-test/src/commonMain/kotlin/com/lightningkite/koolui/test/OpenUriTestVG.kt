package com.lightningkite.koolui.test

import com.lightningkite.kommon.string.Uri
import com.lightningkite.koolui.ExternalAccess
import com.lightningkite.koolui.views.basic.*
import com.lightningkite.koolui.views.interactive.*
import com.lightningkite.koolui.views.layout.*

class OpenUriTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Open URI Test"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "Let's test opening Google!")
            -button("Google") {
                ExternalAccess.openUri(Uri("https://google.com"))
            }
        }
    }
}