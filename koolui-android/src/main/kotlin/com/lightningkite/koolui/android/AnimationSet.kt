package com.lightningkite.koolui.android


import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator

/**
 * A set of animation lambdas for transitioning between two views.
 * Created by jivie on 8/6/15.
 */
data class AnimationSet(
    /**
     * An animation lambda for animating the new view in.
     * This - The view being animated.
     * parent - the view group that contains it.
     * @return A [ViewPropertyAnimator] ready to animate the view.  [ViewPropertyAnimator.start] needs to be called on this value to start the animation.
     */
    val animateIn: View.(parent: ViewGroup) -> ViewPropertyAnimator,
    /**
     * An animation lambda for animating the old view out.
     * This - The view being animated.
     * parent - the view group that contains it.
     * @return A [ViewPropertyAnimator] ready to animate the view.  [ViewPropertyAnimator.start] needs to be called on this value to start the animation.
     */
    val animateOut: View.(parent: ViewGroup) -> ViewPropertyAnimator
) {

    companion object {
        /**
         * Cross-fades between views.
         */
        val fade: AnimationSet = AnimationSet(
            animateIn = {
                alpha = 0f
                animate().alpha(1f).setDuration(300)
            },
            animateOut = {
                alpha = 1f
                animate().alpha(0f).setDuration(300)
            }
        )
        /**
         * Slides the old view out to the left, slides the new view in from the right.
         * Primarily used for moving forward through screens in an app
         */
        val slidePush: AnimationSet = AnimationSet(
            animateIn = {
                translationX = it.width.toFloat()
                animate().translationX(0f).setDuration(300)
            },
            animateOut = {
                translationX = 0f
                animate().translationX(-it.width.toFloat()).setDuration(300)
            }
        )
        /**
         * Slides the old view out to the right, slides the new view in from the left.
         * Primarily used for moving backwards through screens in an app
         */
        val slidePop: AnimationSet = AnimationSet(
            animateIn = {
                translationX = -it.width.toFloat()
                animate().translationX(0f).setDuration(300)
            },
            animateOut = {
                translationX = 0f
                animate().translationX(it.width.toFloat()).setDuration(300)
            }
        )
        /**
         * Slides the old view out upwards, slides the new view in from the bottom.
         */
        val slideUp: AnimationSet = AnimationSet(
            animateIn = {
                translationY = it.height.toFloat()
                animate().translationY(0f).setDuration(300)
            },
            animateOut = {
                translationY = 0f
                animate().translationY(-it.height.toFloat()).setDuration(300)
            }
        )
        /**
         * Slides the old view out downwards, slides the new view in from the top.
         */
        val slideDown: AnimationSet = AnimationSet(
            animateIn = {
                translationY = (-it.height.toFloat())
                animate().translationY(0f).setDuration(300)
            },
            animateOut = {
                translationY = 0f
                animate().translationY(it.height.toFloat()).setDuration(300)
            }
        )
        /**
         * Seemingly flips the view such that the old view is replaced by the new view.
         */
        val flipVertical: AnimationSet = AnimationSet(
            animateIn = {
                scaleY = (0f)
                animate().scaleY(1f).setDuration(150).setStartDelay(150)
            },
            animateOut = {
                scaleY = (1f)
                animate().scaleY(0f).setDuration(150)
            }
        )
    }
}