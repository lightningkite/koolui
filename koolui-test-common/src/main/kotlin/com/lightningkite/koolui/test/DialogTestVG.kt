package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.geometry.LinearPlacement
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator

class DialogTestVG<VIEW>() : ViewGenerator<ViewFactory<VIEW>, VIEW> {
    override val title: String = "Dialogs"

    override fun generate(dependency: ViewFactory<VIEW>): VIEW = with(dependency) {
        scrollVertical(vertical {
            - button(label = "Launch Dialog"){
                launchDialog { dismiss ->
                    card(vertical {
                        - text(text = "Hello world!")
                        - button(label = "I'm good now"){
                            dismiss()
                        }
                    })
                }
            }
            - button(label = "Launch Selector"){
                launchSelector("Select something!", listOf(
                        "First" to { println("First") },
                        "Second" to { println("Second") },
                        "Third" to { println("Third") }
                ))
            }
        })
    }
}
