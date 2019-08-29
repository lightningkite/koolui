package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement
import kotlin.math.abs

abstract class BaseDimensionLayout: DimensionLayout {

    override var onLayoutRequest: () -> Unit = {}

    open fun childInvalidatesMeasurement(fromChild: DimensionLayout): Boolean = true
    open fun childInvalidatesLayout(fromChild: DimensionLayout): Boolean = true

    abstract fun measure(output: Measurement)
    abstract fun layoutChildren(size: Float)

    abstract val childSequence: Sequence<DimensionLayout>

    override var onPlacement: (start: Float, end: Float) -> Unit = { _, _ -> }
    override var parent: DimensionLayout? = null

    private var needsMeasure: Boolean = true
    private val myMeasurement: Measurement = Measurement()
    override val measurement: Measurement
        get() {
            if (needsMeasure) {
                measure(myMeasurement)
                needsMeasure = false
            }
            return myMeasurement
        }

    override fun requestMeasurement() {
        needsMeasure = true
        parent?.childNeedsMeasurement(this)
    }

    override var start: Float = 0f
    override var end: Float = 0f

    var _needsLayout: Boolean = true
    var _childNeedsLayout: Boolean = true
    override val needsLayout: Boolean get() = _needsLayout
    override val childNeedsLayout: Boolean get() = _childNeedsLayout

    override fun requestLayout() {
        if(_needsLayout) return
        _needsLayout = true
        _childNeedsLayout = true
        parent?.childNeedsLayout(this) ?: onLayoutRequest()
    }

    private var previousSize: Float = end - start
    override fun refresh(): Boolean {
        val newSize = size
        if (_needsLayout || abs(previousSize - newSize) > .001) {
            _needsLayout = false
            _childNeedsLayout = false
            previousSize = newSize
            layoutChildren(newSize)
            return true
        } else if (_childNeedsLayout) {
            var result = false
            for (child in childSequence) {
                result = child.refresh() || result
            }
            _childNeedsLayout = false
            return result
        }
        return false
    }

    override fun layout(start: Float, end: Float): Boolean {
        this.start = start
        this.end = end
        onPlacement(start, end)
        return refresh()
    }

    final override fun childNeedsMeasurement(fromChild: DimensionLayout) {
        if(childInvalidatesMeasurement(fromChild)){
            requestMeasurement()
        }
    }

    final override fun childNeedsLayout(fromChild: DimensionLayout?) {
        if(fromChild != null && childInvalidatesLayout(fromChild)){
            requestLayout()
        } else if(!_childNeedsLayout){
            _childNeedsLayout = true
            parent?.childNeedsLayout(null) ?: onLayoutRequest()
        }
    }
}