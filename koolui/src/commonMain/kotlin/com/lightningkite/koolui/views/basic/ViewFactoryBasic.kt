package com.lightningkite.koolui.views.basic

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.reacktive.property.ConstantObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.*

interface ViewFactoryBasic<VIEW> {
    /**
     * Shows a piece of text at the given size.
     */
    fun text(
            text: ObservableProperty<String>,
            importance: Importance = Importance.Normal,
            size: TextSize = TextSize.Body,
            align: AlignPair = AlignPair.CenterLeft,
            maxLines: Int = Int.MAX_VALUE
    ): VIEW

    /**
     * Shows an image with the given scaling.
     * While loading, it will show a loading indicator.
     */
    fun image(
            imageWithOptions: ObservableProperty<ImageWithOptions>
    ): VIEW

    /**
     * A working indicator.
     */
    fun work(): VIEW = text(ConstantObservableProperty("Working..."))

    /**
     * Shows the given view if not working, otherwise shows a progress indicator.
     * @param progress A number 0-1 showing the amount of progress made on a task.  When the value is 1,
     */
    fun progress(progress: ObservableProperty<Float>): VIEW = text(progress.transform { it.times(100).toInt().toString() + "%" })

    /**
     * Adds a background to the given item.
     */
    fun VIEW.background(
            color: ObservableProperty<Color>
    ): VIEW

    fun VIEW.background(
            color: Color
    ): VIEW = background(ConstantObservableProperty(color))

    /**
     * Changes the alpha of a view.
     */
    fun VIEW.alpha(
            alpha: ObservableProperty<Float>
    ): VIEW

    fun VIEW.alpha(
            alpha: Float
    ): VIEW = alpha(ConstantObservableProperty(alpha))
}

