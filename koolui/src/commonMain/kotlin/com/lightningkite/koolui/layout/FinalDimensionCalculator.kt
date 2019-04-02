package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

inline fun FinalDimensionCalculator(crossinline measure: (Measurement) -> Unit): BaseDimensionCalculator {
    return object : BaseDimensionCalculator() {
        override fun measure(output: Measurement) = measure(output)
        override fun layoutChildren(size: Float) {}
    }
}


/*
CASES TO HANDLE

Implementation in platform
Margin absorption for linear within linear layout
Child changes size


TODO

MarginLayout overrides margin output
WidthLayout overrides width output
HeightLayout overrides height output
VerticalLayout
HorizontalLayout
FrameLayout
ScrollLayout


*/

//interface Layout<out MyView : View, View> {
//    //Inherent, potentially mutating properties
//    val underlyingView: ViewAdapter<MyView, View>
//    val lifecycle: TreeObservableProperty
//    val boundsInParent: Rectangle
//    var parentReference: Layout<*, View>?
//
//    //Requests to parent layout about positioning
//    val desiredMargins: Rectangle
//    var visible: Boolean
//
//    val calculatedMargins: Rectangle
//
//    //Calculations for layout
//    /**Returns the size required for this particular view.  Should probably be cached and made dirty during invalidation.**/
//    val requiredSizeX: Float
//    /**Returns the size required for this particular view.  Should probably be cached and made dirty during invalidation.**/
//    val requiredSizeY: Float
//
//    /**Modifies the left and right sides of the bounds of the children.**/
//    fun layoutY(setSpace: Float)
//
//    /**Modifies the top and bottom sides of the bounds of the children.**/
//    fun layoutX(setSpace: Float)
//
//    /**Pass upwards OR start layout, depending on sizing rules for child**/
//    fun invalidateX(fromChild: Layout<*, View>? = null)
//
//    /**Pass upwards OR start layout, depending on sizing rules for child**/
//    fun invalidateY(fromChild: Layout<*, View>? = null)
//}
//
//class FrameLayout<out MyView : View, View>(
//        override val underlyingView: ViewAdapter<MyView, View>,
//        val children: List<Pair<AlignPair, Layout<*, View>>>
//) : Layout<MyView, View> {
//    override val lifecycle = TreeObservableProperty()
//    override val boundsInParent = Rectangle()
//    override var parentReference: Layout<*, View>? = null
//    override val desiredMargins: Rectangle = Rectangle()
//    val absorbedMargins = Rectangle()
//    override val calculatedMargins: Rectangle = Rectangle()
//    override var visible: Boolean = true
//
//    val requiredSizeXDelegate = InvalidatingPropertyDelegate {
//        //Absorb the calculatedMargins of children
//        absorbedMargins.left = children.asSequence().map { it.second.calculatedMargins.left }.min() ?: 0f
//        calculatedMargins.left = absorbedMargins.left + desiredMargins.left
//        absorbedMargins.right = children.asSequence().map { it.second.calculatedMargins.right }.min() ?: 0f
//        calculatedMargins.right = absorbedMargins.right + desiredMargins.right
//        children.asSequence()
//                .map { it.second.requiredSizeX + it.second.calculatedMargins.left + it.second.calculatedMargins.right }
//                .max()?.minus(absorbedMargins.left + absorbedMargins.right) ?: 0f
//    }
//    override val requiredSizeX: Float by requiredSizeXDelegate
//
//    val requiredSizeYDelegate = InvalidatingPropertyDelegate {
//        //Absorb the calculatedMargins of children
//        absorbedMargins.top = children.asSequence().map { it.second.calculatedMargins.top }.min() ?: 0f
//        calculatedMargins.top = absorbedMargins.top + desiredMargins.top
//        absorbedMargins.bottom = children.asSequence().map { it.second.calculatedMargins.bottom }.min() ?: 0f
//        calculatedMargins.bottom = absorbedMargins.bottom + desiredMargins.bottom
//        children.asSequence()
//                .map { it.second.requiredSizeY + it.second.calculatedMargins.top + it.second.calculatedMargins.bottom }
//                .max()?.minus(absorbedMargins.top + absorbedMargins.bottom) ?: 0f
//    }
//    override val requiredSizeY: Float by requiredSizeYDelegate
//    override fun invalidateX(fromChild: Layout<*, View>?) {
//        requiredSizeXDelegate.dirty = true
//        parentReference?.invalidateX(this) ?: layoutX(boundsInParent.width)
//    }
//
//    override fun invalidateY(fromChild: Layout<*, View>?) {
//        requiredSizeYDelegate.dirty = true
//        parentReference?.invalidateY(this) ?: layoutY(boundsInParent.height)
//    }
//
//    override fun layoutX(setSpace: Float) {
//        for (child in children) {
//            child.second.boundsInParent.left = when (child.first.horizontal) {
//                Align.Start, Align.Fill -> child.second.calculatedMargins.left - calculatedMargins.left
//                Align.Center -> setSpace / 2 - child.second.requiredSizeX
//                Align.End -> setSpace - child.second.calculatedMargins.right + calculatedMargins.right - child.second.requiredSizeX
//            }
//            child.second.boundsInParent.right = when (child.first.horizontal) {
//                Align.Start -> child.second.calculatedMargins.left - calculatedMargins.left + child.second.requiredSizeX
//                Align.Center -> setSpace / 2 + child.second.requiredSizeX
//                Align.End, Align.Fill -> setSpace - child.second.calculatedMargins.right + calculatedMargins.right
//            }
//        }
//    }
//
//    override fun layoutY(setSpace: Float) {
//        for (child in children) {
//            child.second.boundsInParent.top = when (child.first.horizontal) {
//                Align.Start, Align.Fill -> child.second.calculatedMargins.top - calculatedMargins.top
//                Align.Center -> setSpace / 2 - child.second.requiredSizeY
//                Align.End -> setSpace - child.second.calculatedMargins.bottom + calculatedMargins.bottom - child.second.requiredSizeY
//            }
//            child.second.boundsInParent.bottom = when (child.first.horizontal) {
//                Align.Start -> child.second.calculatedMargins.top - calculatedMargins.top + child.second.requiredSizeY
//                Align.Center -> setSpace / 2 + child.second.requiredSizeY
//                Align.End, Align.Fill -> setSpace - child.second.calculatedMargins.bottom + calculatedMargins.bottom
//            }
//        }
//    }
//}