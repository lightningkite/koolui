package com.lightningkite.koolui.view.layout

import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import com.lightningkite.koolui.android.AndroidLayoutAdapter
import com.lightningkite.koolui.android.IntrinsicDimensionLayouts
import com.lightningkite.koolui.android.LayoutToAndroidView
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.views.LayoutVFLayout
import com.lightningkite.koolui.layout.views.LayoutViewWrapper
import com.lightningkite.koolui.view.HasActivityAccess
import com.lightningkite.reacktive.property.MutableObservableProperty

interface LayoutAndroidLayout : LayoutVFLayout<View>, LayoutViewWrapper<View>, HasActivityAccess {

    override fun scrollBoth(view: Layout<*, View>, amountX: MutableObservableProperty<Float>, amountY: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(HorizontalScrollView(context).apply {
            addView(ScrollView(context))
        }, true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }

    override fun scrollVertical(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(ScrollView(context), true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }

    override fun scrollHorizontal(view: Layout<*, View>, amount: MutableObservableProperty<Float>): Layout<*, View> {
        val adapter = AndroidLayoutAdapter(HorizontalScrollView(context), true)
        val intrinsic = IntrinsicDimensionLayouts(adapter.view)
        val layout = Layout(
                viewAdapter = adapter,
                x = intrinsic.x,
                y = intrinsic.y
        )
        adapter.view.addView(LayoutToAndroidView(context).also { it.layout = view })
        return layout
    }
}