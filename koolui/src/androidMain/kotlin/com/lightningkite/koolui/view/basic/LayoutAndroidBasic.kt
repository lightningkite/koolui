package com.lightningkite.koolui.view.basic

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.lightningkite.koolui.android.android
import com.lightningkite.koolui.android.dip
import com.lightningkite.koolui.android.sp
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageScaleType
import com.lightningkite.koolui.image.ImageWithOptions
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.layout.views.wrap
import com.lightningkite.koolui.layout.views.intrinsicLayout
import com.lightningkite.koolui.view.HasActivityAccess
import com.lightningkite.koolui.views.Themed
import com.lightningkite.koolui.views.basic.ViewFactoryBasic
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind


interface LayoutAndroidBasic: ViewFactoryBasic<Layout<*, View>>, LayoutViewWrapper<View>, Themed, HasActivityAccess {

    override fun text(
            text: ObservableProperty<String>,
            importance: Importance,
            size: TextSize,
            align: AlignPair,
            maxLines: Int
    ) = intrinsicLayout(TextView(activityAccess.context)) { layout ->
        textSize = size.sp()
        layout.isAttached.bind(text) {
            this.text = it
            layout.requestMeasurement()
        }
        setTextColor(colorSet.importance(importance).toInt())
        gravity = align.android()
        setMaxLines(maxLines)
    }

    override fun image(imageWithOptions: ObservableProperty<ImageWithOptions>): Layout<*, View> = intrinsicLayout(ImageView(activityAccess.context)) { layout ->
        layout.isAttached.bind(imageWithOptions) {
            this.scaleType = when (it.scaleType) {
                ImageScaleType.Crop -> ImageView.ScaleType.CENTER_CROP
                ImageScaleType.Fill -> ImageView.ScaleType.FIT_CENTER
                ImageScaleType.Center -> ImageView.ScaleType.CENTER
            }
            val drawable = it.android()
            setImageDrawable(drawable)
            it.defaultSize?.let {
                minimumWidth = (it.x * dip).toInt()
                minimumHeight = (it.y * dip).toInt()
            }
            layout.requestMeasurement()
        }
    }

    override fun work(): Layout<*, View> = wrap(ProgressBar(activityAccess.context).apply { isIndeterminate = true })

    override fun progress(progress: ObservableProperty<Float>): Layout<*, View> = intrinsicLayout(ProgressBar(activityAccess.context, null, android.R.attr.progressBarStyleHorizontal)) { layout ->
        isIndeterminate = false
        max = 100
        layout.isAttached.bind(progress) {
            this.setProgress((it * 100).toInt(), true)
        }
    }

    override fun Layout<*, View>.background(color: ObservableProperty<Color>): Layout<*, View> {
        isAttached.bind(color) {
            viewAdapter.viewAsBase.setBackgroundColor(it.toInt())
        }
        return this
    }

    override fun Layout<*, View>.alpha(alpha: ObservableProperty<Float>): Layout<*, View> {
        isAttached.bind(alpha) {
            viewAdapter.viewAsBase.alpha = it
        }
        return this
    }
}