package com.lightningkite.koolui.android


import android.view.View
import android.view.ViewGroup

/**
 * Helps one swap views in and out of a container.
 *
 * Created by joseph on 1/19/18.
 */
class ViewSwapManager<VG : ViewGroup>(
    /**
     * The parent view to manage.
     */
    val parent: VG,
    /**
     * A lambda that generates the default layout parameters for views being swapped in.
     */
    val generateLayoutParams: () -> ViewGroup.LayoutParams
) {
    var currentView: View? = null

    fun swap(newView: View, animation: AnimationSet = AnimationSet.fade) {
        val oldView = currentView
        if (newView == oldView) return

        parent.addView(newView, generateLayoutParams())
        if (oldView != null) {
            //animate out old view
            animation.animateOut(oldView, parent).withEndAction { parent.removeView(oldView) }.start()

            //animate in new view
            animation.animateIn(newView, parent).start()
        }
        currentView = newView
    }
}