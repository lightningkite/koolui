package com.lightningkite.koolui.test

import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.koolui.ExternalAccess
import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.reacktive.property.StandardObservableProperty
import kotlinx.io.core.toByteArray

class FilesTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Files Test"

    val data = StandardObservableProperty<String>("")

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        vertical {
            -text(text = "File contents:")
            -textArea(data)
            -button("Load") {
                ExternalAccess.loadFromChosenFile {
                    data.value = it?.let { kotlinx.io.core.String(it) } ?: ""
                }
            }
            -button("Save"){
                ExternalAccess.saveToChosenFile("Test", MediaTypeWithDescription.txt, data.value.toByteArray())
            }
        }
    }
}

