package com.lightningkite.koolui.test

import com.lightningkite.koolui.builders.button
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.builders.vertical

class DialogTestVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Dialogs"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        scrollVertical(vertical {
            -button(label = "Launch Dialog") {
                launchDialog { dismiss ->
                    card(vertical {
                        -text(text = "Hello world!")
                        -button(label = "I'm good now") {
                            println("I was clicked!")
                            dismiss()
                        }
                    })
                }
            }
            -button(label = "Launch Dialog B") {
                launchDialog { dismiss ->
                    text(text = "HELLO.")
                }
            }
            -button(label = "Launch Selector") {
                launchSelector("Select something!", listOf(
                        "First" to { println("First") },
                        "Second" to { println("Second") },
                        "Third" to { println("Third") }
                ))
            }
        })
    }
}
