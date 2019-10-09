package com.lightningkite.koolui.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lightningkite.koolui.android.*
import com.lightningkite.koolui.concepts.Animation
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import com.lightningkite.koolui.layout.views.LayoutViewWrapper

interface LayoutAndroidWrapper: LayoutViewWrapper<View>, HasActivityAccess {

    override fun intrinsicDimensionLayouts(view: View): LeafDimensionLayouts = IntrinsicDimensionLayouts(view)

    override fun applyEntranceTransition(view: View, animation: Animation) {
        val parent = view.parent as? ViewGroup ?: return
        animation.android().animateIn.invoke(view, parent)
    }

    override fun applyExitTransition(view: View, animation: Animation, onComplete: () -> Unit) {
        val parent = view.parent as? ViewGroup ?: run {
            onComplete()
            return
        }
        animation.android().animateOut.invoke(view, parent).withEndAction(onComplete)
    }

    override fun defaultViewContainer(): View = ManualLayout(context)

    override fun <SPECIFIC : View> SPECIFIC.adapter() = AndroidLayoutAdapter(this, true)


    override fun nativeViewAdapter(wraps: Layout<*, View>): View = LayoutToAndroidView(context).apply {
        this.layout = wraps
    }
}