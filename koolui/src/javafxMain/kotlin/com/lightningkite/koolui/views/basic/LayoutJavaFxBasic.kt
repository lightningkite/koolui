package com.lightningkite.koolui.views.basic

import com.jfoenix.controls.JFXProgressBar
import com.jfoenix.controls.JFXSpinner
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.async.scope
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.wrap
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.views.HasScale
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.javafx
import com.lightningkite.koolui.views.toJavaFX
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.text.Font
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface LayoutJavaFxBasic: ViewFactoryBasic<Layout<*, Node>>, LayoutViewWrapper<Node>, Themed, HasScale {
    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ) = intrinsicLayout(Label()) { layout ->
        font = Font.font(size.javafx)
        textFill = when (importance) {
            Importance.Low -> colorSet.foregroundDisabled.toJavaFX()
            Importance.Normal -> colorSet.foreground.toJavaFX()
            Importance.High -> colorSet.foregroundHighlighted.toJavaFX()
            Importance.Danger -> Color.red.toJavaFX()
        }
        alignment = align.javafx
        isWrapText = true

        layout.isAttached.bind(text) {
            if (maxLines != Int.MAX_VALUE) {
                val cap = maxLines * 80
                this.text = if (it.length > cap) it.take(cap) + "..." else it
            } else {
                this.text = it
            }
            layout.requestMeasurement()
        }
    }

    override fun image(imageWithOptions: ObservableProperty<ImageWithOptions>): Layout<*, Node> = intrinsicLayout(ImageView()) { layout ->
        layout.isAttached.bind(imageWithOptions) {
            layout.isAttached.scope.launch(Dispatchers.UI) {
                it.defaultSize?.x?.times(scale)?.let { this@wrapWithLayout.fitWidth = it }
                it.defaultSize?.y?.times(scale)?.let { this@wrapWithLayout.fitHeight = it }
                //TODO: Scale type
                this@wrapWithLayout.image = it.image.get(scale.toFloat(), it.defaultSize)

                layout.requestMeasurement()
            }
        }
    }

    override fun work(): Layout<*, Node> {
        val spinner = JFXSpinner().apply {
            style = "-fx-stroke: ${colorSet.foreground.toWeb()}"
            isVisible = true
            minWidth = 30.0 * scale
            minHeight = 30.0 * scale
            prefWidth = 30.0 * scale
            prefHeight = 30.0 * scale
        }
        return wrap(spinner)
    }

    override fun progress(progress: ObservableProperty<Float>): Layout<*, Node> {
        return wrap(JFXProgressBar()){ lifecycle ->
            style = "-fx-stroke: ${colorSet.foreground.toWeb()}"
            lifecycle.bind(progress) {
                this.progress = it.toDouble()
            }
        }
    }

    override fun Layout<*, Node>.background(color: ObservableProperty<Color>): Layout<*, Node> {
        val v = viewAsBase
        if (v is Region) {
            isAttached.bind(color) {
                v.background = Background(BackgroundFill(it.toJavaFX(), CornerRadii.EMPTY, Insets.EMPTY))
            }
        }
        return this
    }

    override fun Layout<*, Node>.alpha(alpha: ObservableProperty<Float>): Layout<*, Node> {
        isAttached.bind(alpha) {
            viewAsBase.opacity = it.toDouble()
            viewAsBase.isVisible = it != 0f
        }
        return this
    }
}