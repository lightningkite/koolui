package com.lightningkite.koolui.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.ViewAdapter
import com.lightningkite.recktangle.Rectangle
import kotlin.math.ceil

class AndroidLayoutAdapter<SPECIFIC : View>(override val view: SPECIFIC, val addView: Boolean) : ViewAdapter<SPECIFIC, View> {
    override val viewAsBase: View = view
    val layoutParams = ManualLayout.LayoutParams(0, 0, 0, 0)

    init {
        view.layoutParams = layoutParams
    }

    override fun updatePlacementX(start: Float, end: Float) {
        this.viewAsBase.layoutParams.width = (end - start).times(dip).toInt()
        (this.viewAsBase.layoutParams as? ManualLayout.LayoutParams)?.let {
            it.left = ceil(dip * start).toInt()
            it.right = ceil(dip * end).toInt()
            viewAsBase.requestLayout()
        }
    }

    override fun updatePlacementY(start: Float, end: Float) {
        this.viewAsBase.layoutParams.height = (end - start).times(dip).toInt()
        (this.viewAsBase.layoutParams as? ManualLayout.LayoutParams)?.let {
            it.top = ceil(dip * start).toInt()
            it.bottom = ceil(dip * end).toInt()
            viewAsBase.requestLayout()
        }
    }

    override fun onAddChild(layout: Layout<*, View>) {
        if (addView && view is ViewGroup) {
            view.addView(layout.viewAdapter.viewAsBase)
        }
    }

    override fun onRemoveChild(layout: Layout<*, View>) {
        if (addView && view is ViewGroup) {
            view.removeView(layout.viewAdapter.viewAsBase)
        }
    }
}