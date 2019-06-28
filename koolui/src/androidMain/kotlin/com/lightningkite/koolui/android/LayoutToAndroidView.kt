package com.lightningkite.koolui.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import com.lightningkite.koolui.color.Color
import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
import com.lightningkite.koolui.layout.DimensionLayout
import com.lightningkite.koolui.layout.Layout
import com.lightningkite.koolui.layout.size
import com.lightningkite.recktangle.Rectangle
import java.lang.IllegalStateException
import kotlin.math.ceil
import kotlin.math.min

@SuppressLint("ViewConstructor")
class LayoutToAndroidView(context: Context) : ViewGroup(context) {

    val isAttached = TreeObservableProperty()

    var layout: Layout<*, View>? = null
        set(value) {
            field = value
            if (childCount > 0) {
                removeAllViews()
            }
            value?.viewAdapter?.viewAsBase?.let { addView(it) }
            value?.isAttached?.parent = isAttached
            value?.x?.requestLayout()
            value?.y?.requestLayout()
            value?.x?.onLayoutRequest = { this.requestLayout() }
            value?.y?.onLayoutRequest = { this.requestLayout() }
            requestLayout()
        }

    var listenersEnabled = true
    fun attachParent(treeObservableProperty: TreeObservableProperty) {
        isAttached.alwaysOn = false
        isAttached.parent = treeObservableProperty
        listenersEnabled = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (listenersEnabled) {
            isAttached.alwaysOn = true
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (listenersEnabled) {
            isAttached.alwaysOn = false
        }
    }

    val rect = Rectangle()
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        rect.right = (right - left) / dip
        rect.bottom = (bottom - top) / dip
        layout?.layout(rect)
        layout?.viewAdapter?.viewAsBase?.measure(
                MeasureSpec.makeMeasureSpec(right - left, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(bottom - top, MeasureSpec.EXACTLY)
        )
        layout?.viewAdapter?.viewAsBase?.layout(0, 0, right - left, bottom - top)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout?.viewAdapter?.viewAsBase?.measure(widthMeasureSpec, heightMeasureSpec)
        val givenWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> min(MeasureSpec.getSize(widthMeasureSpec) / dip, layout?.x?.measurement?.size ?: 1f)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec) / dip
            MeasureSpec.UNSPECIFIED -> layout?.x?.measurement?.size ?: 1f
            else -> throw IllegalStateException()
        }
        rect.right = rect.left + givenWidth
        layout?.x?.layout(rect.left, rect.right)
        val givenHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> min(MeasureSpec.getSize(heightMeasureSpec) / dip, layout?.y?.measurement?.size ?: 1f)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec) / dip
            MeasureSpec.UNSPECIFIED -> layout?.y?.measurement?.size ?: 1f
            else -> throw IllegalStateException()
        }
        rect.bottom = rect.top + givenHeight
        layout?.y?.layout(rect.top, rect.bottom)
        layout?.layout(rect)
        setMeasuredDimension(
                layout?.x?.size?.times(dip)?.let { ceil(it) }?.toInt() ?: 1,
                layout?.y?.size?.times(dip)?.let { ceil(it) }?.toInt() ?: 1
        )
//        for(childIndex in 0 until childCount){
//            getChildAt(childIndex).measure(widthMeasureSpec, heightMeasureSpec)
//        }
//        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
//        val givenHeight = MeasureSpec.getSize(heightMeasureSpec)
//        when(MeasureSpec.getMode(widthMeasureSpec)){
//            MeasureSpec.AT_MOST,
//            MeasureSpec.EXACTLY -> layout.x.start
//            else -> throw IllegalArgumentException()
//        }
//        setMeasuredDimension(
//                when(MeasureSpec.getMode(widthMeasureSpec)){
//                    MeasureSpec.AT_MOST -> Math.min(givenWidth, layout?.x.androidSize())
//                    MeasureSpec.EXACTLY -> givenWidth
//                    MeasureSpec.UNSPECIFIED -> layout?.x.androidSize()
//                    else -> throw IllegalArgumentException()
//                },
//                when(MeasureSpec.getMode(heightMeasureSpec)){
//                    MeasureSpec.AT_MOST -> Math.min(givenHeight, layout?.y.androidSize())
//                    MeasureSpec.EXACTLY -> givenHeight
//                    MeasureSpec.UNSPECIFIED -> layout?.y.androidSize()
//                    else -> throw IllegalArgumentException()
//                }
//        )
    }
}