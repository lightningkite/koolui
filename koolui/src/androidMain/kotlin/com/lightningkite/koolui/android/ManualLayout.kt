package com.lightningkite.koolui.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lightningkite.koolui.layout.Layout

class ManualLayout(context: Context) : ViewGroup(context) {
    class LayoutParams(
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
    ): ViewGroup.LayoutParams(0, 0) {
        var left: Int = left
            set(value){
                field = value
                width = right - left
            }
        var top: Int = top
            set(value){
                field = value
                height = bottom - top
            }
        var right: Int = right
            set(value){
                field = value
                width = right - left
            }
        var bottom: Int = bottom
            set(value){
                field = value
                height = bottom - top
            }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for(i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            val params = child.layoutParams as LayoutParams
            child.layout(params.left, params.top, params.right, params.bottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for(i in 0 until this.childCount) {
            val child = this.getChildAt(i)
            val params = child.layoutParams as LayoutParams
            child.measure(
                    MeasureSpec.makeMeasureSpec(params.right - params.left, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(params.bottom - params.top, MeasureSpec.EXACTLY)
            )
        }
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec)
        )
    }
}

