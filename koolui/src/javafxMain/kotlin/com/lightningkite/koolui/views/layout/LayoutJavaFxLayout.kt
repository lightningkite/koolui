package com.lightningkite.koolui.views.layout

import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutVFLayout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import javafx.scene.Node
import javafx.scene.control.ScrollPane

interface LayoutJavaFxLayout : LayoutVFLayout<Node>, LayoutViewWrapper<Node> {
    override fun scrollVertical(view: Layout<*, Node>, amount: MutableObservableProperty<Float>): Layout<*, Node> = intrinsicLayout(ScrollPane(nativeViewAdapter(view))) { layout ->
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        isFitToWidth = true
        layout.addSemiChild(view)

        var suppress = false
        layout.isAttached.bind(amount) {
            if (suppress) {
                return@bind
            }
            suppress = true
            vvalue = it.toDouble()
            suppress = false
        }
        this.setOnScrollFinished {
            if (suppress) {
                return@setOnScrollFinished
            }
            suppress = true
            amount.value = vvalue.toFloat()
            suppress = false
        }
    }

    override fun scrollHorizontal(view: Layout<*, Node>, amount: MutableObservableProperty<Float>): Layout<*, Node> = intrinsicLayout(ScrollPane(nativeViewAdapter(view))) { layout ->
        hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        isFitToHeight = true
        layout.addSemiChild(view)

        var suppress = false
        layout.isAttached.bind(amount) {
            if (suppress) {
                return@bind
            }
            suppress = true
            hvalue = it.toDouble()
            suppress = false
        }
        this.setOnScrollFinished {
            if (suppress) {
                return@setOnScrollFinished
            }
            suppress = true
            amount.value = hvalue.toFloat()
            suppress = false
        }
    }

    override fun scrollBoth(view: Layout<*, Node>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): Layout<*, Node> = intrinsicLayout(ScrollPane(nativeViewAdapter(view))) { layout ->
        hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        style = "-fx-background-color: transparent; -fx-background: transparent;"
        layout.addSemiChild(view)

        var suppress = false
        layout.isAttached.bind(amountX) {
            if (suppress) {
                return@bind
            }
            suppress = true
            hvalue = it.toDouble()
            suppress = false
        }
        layout.isAttached.bind(amountY) {
            if (suppress) {
                return@bind
            }
            suppress = true
            vvalue = it.toDouble()
            suppress = false
        }
        this.setOnScrollFinished {
            if (suppress) {
                return@setOnScrollFinished
            }
            suppress = true
            amountX.value = hvalue.toFloat()
            amountY.value = vvalue.toFloat()
            suppress = false
        }
    }
}