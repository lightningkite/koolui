package com.larvalabs.svgandroid

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.util.Log
import com.lightningkite.koolui.ApplicationAccess

/**
 * Based on work by nimingtao:
 * https://code.google.com/p/adet/source/browse/trunk/adet/src/cn/mobileww/adet/graphics/SvgDrawable.java
 *
 * @author nimingtao, mstevens83
 * @since 19 Aug 2013
 */
class SVGDrawable
/**
 * @param svg
 */
(svg: SVG) : PictureDrawable(svg.picture) {

    private val TAG = "SVGDrawable"

    private val mSvgState: SVGState

    init {
        this.mSvgState = SVGState(svg)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
    }

    /**
     *
     *
     * Original author nimingtao wrote that this method may not work on devices with Ice Cream Sandwich (Android v4.0).
     *
     *
     *
     * See: http://stackoverflow.com/q/10384613/1084488
     *
     *
     *
     * Apparently this is because canvas.drawPicture is not supported with hardware acceleration. If the problem occurs
     * and solved by programmatically turning off hardware acceleration only on the view that will draw the Picture:
     * view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
     *
     *
     *
     * However, I (mstevens83) was unable to reproduce this problem on an emulator running Ice Cream Sandwich, nor on
     * physical devices running Jelly Bean (v4.1.2 and v4.3 tested).
     *
     */
    override fun draw(canvas: Canvas) {
        if (picture != null) {
            val bounds = bounds
            canvas.save()
            // draw picture to fit boundsInParent!
            canvas.drawPicture(picture, bounds)
            canvas.restore()
        }
    }

    override fun getIntrinsicWidth(): Int {
        return (picture.width / SVG.MULTIPLIER * ApplicationAccess.dip).toInt()
    }

    override fun getIntrinsicHeight(): Int {
        return (picture.height / SVG.MULTIPLIER * ApplicationAccess.dip).toInt()
    }

    // @Override
    // public int getIntrinsicWidth() {
    // Rect boundsInParent = getBoundsInParent();
    // RectF limits = mSvgState.mSvg.getLimits();
    // if (boundsInParent != null) {
    // return (int) boundsInParent.width();
    // } else if (limits != null) {
    // return (int) limits.width();
    // } else {
    // return -1;
    // }
    // }
    //
    // @Override
    // public int getIntrinsicHeight() {
    // Rect boundsInParent = getBoundsInParent();
    // RectF limits = mSvgState.mSvg.getLimits();
    // if (boundsInParent != null) {
    // return (int) boundsInParent.height();
    // } else if (limits != null) {
    // return (int) limits.height();
    // } else {
    // return -1;
    // }
    // }

    override fun getChangingConfigurations(): Int {
        val c = super.getChangingConfigurations() or mSvgState.mChangingConfigurations
        Log.e(TAG, "CC = $c")
        return c
    }

    override fun getConstantState(): Drawable.ConstantState? {
        mSvgState.mChangingConfigurations = super.getChangingConfigurations()
        return this.mSvgState
    }

    class SVGState constructor(private val mSvg: SVG) : Drawable.ConstantState() {
        var mChangingConfigurations: Int = 0

        /*
         * (non-Javadoc)
         * @see android.graphics.drawable.Drawable.ConstantState#newDrawable()
         */
        override fun newDrawable(): Drawable {
            return SVGDrawable(mSvg)
        }

        /*
         * (non-Javadoc)
         * @see android.graphics.drawable.Drawable.ConstantState#getChangingConfigurations()
         */
        override fun getChangingConfigurations(): Int {
            return mChangingConfigurations
        }

    }

}
