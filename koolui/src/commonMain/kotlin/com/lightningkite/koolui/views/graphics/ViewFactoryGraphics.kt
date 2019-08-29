package com.lightningkite.koolui.views.graphics

import com.lightningkite.koolui.canvas.Canvas
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.reacktive.property.ObservableProperty

interface ViewFactoryGraphics<VIEW> {
    /**
     * A canvas you can draw on.
     */
    fun canvas(
            draw: ObservableProperty<Canvas.() -> Unit>
    ): VIEW
}