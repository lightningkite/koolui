package com.lightningkite.koolui.views

import com.jfoenix.controls.JFXButton
import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.recktangle.Point
import com.lightningkite.recktangle.Rectangle
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region

interface JavaFxLayoutWrapper : LayoutViewWrapper<Node> {

    override fun nativeViewAdapter(wraps: Layout<*, Node>): Pane = object : Pane() {
        val rect = Rectangle()

        init {
            children.add(wraps.viewAsBase)
            wraps.x.onLayoutRequest = {
                this.requestLayout()
            }
            wraps.y.onLayoutRequest = {
                this.requestLayout()
            }
        }

        override fun layoutChildren() {
            rect.left = 0f
            rect.top = 0f
            rect.right = width.toFloat()
            rect.bottom = height.toFloat()
            wraps.layout(rect)
            wraps.viewAsBase.relocate(0.0, 0.0)
            wraps.viewAsBase.resize(width, height)
        }

        override fun computePrefWidth(height: Double): Double {
            return wraps.x.measurement.size.toDouble()
        }

        override fun computePrefHeight(width: Double): Double {
            return wraps.y.measurement.size.toDouble()
        }
    }

    override fun defaultViewContainer(): Node = Pane()

    override fun <SPECIFIC : Node> SPECIFIC.adapter(): ViewAdapter<SPECIFIC, Node> {
        isManaged = false
        return object : ViewAdapter<SPECIFIC, Node> {
            override val view: SPECIFIC = this@adapter
            override val viewAsBase: Node get() = view
            var currentWidth = 0f
            var currentHeight = 0f
            val clipRect = javafx.scene.shape.Rectangle()

            init {
                view.clip = clipRect
            }

            override fun updatePlacementX(start: Float, end: Float) {
                view.layoutX = start.toDouble()
                currentWidth = end - start
                view.resize(currentWidth.toDouble(), currentHeight.toDouble())
                clipRect.width = currentWidth.toDouble()
            }

            override fun updatePlacementY(start: Float, end: Float) {
                view.layoutY = start.toDouble()
                currentHeight = end - start
                view.resize(currentWidth.toDouble(), currentHeight.toDouble())
                clipRect.height = currentHeight.toDouble()
                view.clip = clipRect
            }

            override fun onAddChild(layout: Layout<*, Node>) {
                (view as? Pane)?.children?.add(layout.viewAsBase)
            }

            override fun onRemoveChild(layout: Layout<*, Node>) {
                (view as? Pane)?.children?.remove(layout.viewAsBase)
            }
        }
    }

    override fun intrinsicDimensionLayouts(view: Node): LeafDimensionLayouts {
        return object : LeafDimensionLayouts() {
            override fun measureX(output: Measurement) {
                output.size = view.prefWidth(0.0).toFloat()
            }

            override fun measureY(xSize: Float, output: Measurement) {
                output.size = view.prefHeight(xSize.toDouble()).toFloat()
            }
        }
    }

    override fun applyEntranceTransition(view: Node, animation: Animation) {
        animation.javaFxIn(view, (view.parent as? Region)?.let { Point(it.width.toFloat(), it.height.toFloat()) }
                ?: Point.zero).play()
    }

    override fun applyExitTransition(view: Node, animation: Animation, onComplete: () -> Unit) {
        animation.javaFxOut(view, (view.parent as? Region)?.let { Point(it.width.toFloat(), it.height.toFloat()) }
                ?: Point.zero).apply {
            setOnFinished {
                onComplete()
            }
        }.play()
    }
}