package com.lightningkite.koolui.android

import android.view.View
import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.geometry.Measurement
import com.lightningkite.koolui.layout.LeafDimensionLayouts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class IntrinsicDimensionLayouts(val view: View): LeafDimensionLayouts() {

//    init {
//        GlobalScope.launch(Dispatchers.UI) {
//            delay(1)
//            println("IntrinsicDimensionLayouts for ${generateSequence(view){ it.parent as? View }.take(8).joinToString("->") { it::class.java.simpleName }}: ${this@IntrinsicDimensionLayouts}")
//        }
//    }

    override fun measureX(output: Measurement) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        output.size = view.measuredWidth.toFloat() / dip + .1f
        output.startMargin = 16f
        output.endMargin = 16f
    }

    override fun measureY(xSize: Float, output: Measurement) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(ceil(xSize * dip).toInt(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        output.size = view.measuredHeight.toFloat() / dip + .1f
        output.startMargin = 16f
        output.endMargin = 16f
    }

}
