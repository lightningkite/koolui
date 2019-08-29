package com.lightningkite.koolui.views

import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import javafx.animation.*
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.util.Duration

val AlignPair.javafx
    get() = when (this) {
        AlignPair.TopLeft -> Pos.TOP_LEFT
        AlignPair.TopCenter -> Pos.TOP_CENTER
        AlignPair.TopFill -> Pos.TOP_CENTER
        AlignPair.TopRight -> Pos.TOP_RIGHT
        AlignPair.CenterLeft -> Pos.CENTER_LEFT
        AlignPair.CenterCenter -> Pos.CENTER
        AlignPair.CenterFill -> Pos.CENTER
        AlignPair.CenterRight -> Pos.CENTER_RIGHT
        AlignPair.FillLeft -> Pos.CENTER_LEFT
        AlignPair.FillCenter -> Pos.CENTER
        AlignPair.FillFill -> Pos.CENTER
        AlignPair.FillRight -> Pos.CENTER_RIGHT
        AlignPair.BottomLeft -> Pos.BOTTOM_LEFT
        AlignPair.BottomCenter -> Pos.BOTTOM_CENTER
        AlignPair.BottomFill -> Pos.BOTTOM_CENTER
        AlignPair.BottomRight -> Pos.BOTTOM_RIGHT
    }

fun Color.toJavaFX() = javafx.scene.paint.Color.rgb(this.redInt, this.greenInt, this.blueInt, this.alpha.toDouble())

val animationDuration = Duration.millis(300.0)
fun Animation.javaFxOut(node: Node, containerSize: Point): Transition = when (this) {
    Animation.None -> PauseTransition().apply {
        duration = Duration.ZERO
    }
    Animation.Push -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromX = 0.0
        toX = -containerSize.x.toDouble()
    }
    Animation.Pop -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromX = 0.0
        toX = containerSize.x.toDouble()
    }
    Animation.MoveUp -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromY = 0.0
        toY = containerSize.y.toDouble()
    }
    Animation.MoveDown -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromY = 0.0
        toY = -containerSize.y.toDouble()
    }
    Animation.Fade -> FadeTransition().apply {
        this.node = node
        duration = animationDuration
        fromValue = 1.0
        toValue = 0.0
    }
    Animation.Flip -> ScaleTransition().apply {
        this.node = node
        duration = Duration.millis(animationDuration.toMillis() / 2)
        fromY = 1.0
        toY = 0.0
    }
}

fun Animation.javaFxIn(node: Node, containerSize: Point): Transition = when (this) {
    Animation.None -> PauseTransition().apply {
        duration = Duration.ZERO
    }
    Animation.Push -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromX = containerSize.x.toDouble()
        toX = 0.0
    }
    Animation.Pop -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromX = -containerSize.x.toDouble()
        toX = 0.0
    }
    Animation.MoveUp -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromY = -containerSize.y.toDouble()
        toY = 0.0
    }
    Animation.MoveDown -> TranslateTransition().apply {
        this.node = node
        duration = animationDuration
        fromY = containerSize.y.toDouble()
        toY = 0.0
    }
    Animation.Fade -> FadeTransition().apply {
        this.node = node
        duration = animationDuration
        fromValue = 0.0
        toValue = 1.0
    }
    Animation.Flip -> ScaleTransition().apply {
        this.node = node
        node.scaleY = 0.0
        delay = Duration.millis(animationDuration.toMillis() / 2)
        duration = Duration.millis(animationDuration.toMillis() / 2)
        fromY = 0.0
        toY = 1.0
    }
}
fun <T> ObservableProperty<Boolean>.bindBidirectional(
        kotlinx: MutableObservableProperty<T>,
        property: Property<T>
) {
    bind(kotlinx) {
        if (it != property.value) {
            property.value = it
        }
    }
    property.addListener { observable, oldValue, newValue ->
        if (newValue != kotlinx.value) {
            kotlinx.value = newValue
        }
    }
}