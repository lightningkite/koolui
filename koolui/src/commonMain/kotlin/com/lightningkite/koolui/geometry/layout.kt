//package com.lightningkite.koolui.geometry
//
//import com.lightningkite.koolui.implementationhelpers.TreeObservableProperty
//import com.lightningkite.reacktive.Lifecycle
//import com.lightningkite.recktangle.Rectangle
//
//inline class SizingBehavior(val value: Float) {
//    companion object {
//        val matchParent = SizingBehavior(Float.NEGATIVE_INFINITY)
//        fun weight(value: Float) = SizingBehavior(-value)
//
//        val wrapContent = SizingBehavior(0f)
//    }
//}
//
//interface ViewAdapter<out MyView: View, View> {
//    val underlyingView: MyView
//    fun updateBounds(bounds: Rectangle)
//    fun addChild(view: View)
//    fun removeChild(view: View)
//}
//
//interface Layout<out MyView: View, View> {
//    //Inherent, potentially mutating properties
//    val underlyingView: ViewAdapter<MyView, View>
//    val lifecycle: Lifecycle
//    val bounds: Rectangle
//    var parentReference: Layout<*, View>?
//
//    //Requests to parent layout about positioning
//    val margins: Rectangle
//    val visible: Boolean
//    val sizingX: SizingBehavior
//    val sizingY: SizingBehavior
//
//    //Calculations for layout
//    val requiredSizeX: Float
//    val requiredSizeY: Float
//    fun layoutX(setSpace: Float)
//    fun layoutY(setSpace: Float)
//    fun invalidateX() {
//        if(sizingX == SizingBehavior.wrapContent) {
//            parentReference?.invalidateX() ?: layoutX(bounds.width)
//        } else {
//            layoutX(bounds.width)
//        }
//    }
//    fun invalidateY() {
//        if(sizingY == SizingBehavior.wrapContent) {
//            parentReference?.invalidateY() ?: layoutY(bounds.height)
//        } else {
//            layoutY(bounds.height)
//        }
//    }
//}
////Invaliding layout when children change?  Has to go all the way up if it is wrap content the whole way
////We can skip if there is static sizing