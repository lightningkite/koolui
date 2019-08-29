//package com.lightningkite.koolui.builders
//
//import com.lightningkite.koolui.geometry.Align
//import com.lightningkite.koolui.geometry.AlignPair
//import com.lightningkite.koolui.geometry.LinearPlacement
//import com.lightningkite.koolui.views.ViewFactory
//
//class LinearBuilder<VIEW>() : MutableList<Pair<LinearPlacement, VIEW>> by ArrayList<Pair<LinearPlacement, VIEW>>() {
//
//    var defaultAlign = Align.Fill
//
//    operator fun VIEW.unaryMinus() {
//        add(LinearPlacement(0f, defaultAlign) to this)
//    }
//
//    operator fun VIEW.unaryPlus() {
//        add(LinearPlacement(1f, defaultAlign) to this)
//    }
//
//    operator fun LinearPlacement.minus(view: VIEW) {
//        add(this to view)
//    }
//    operator fun LinearPlacement.plus(view: VIEW) {
//        add(this to view)
//    }
//}
//
//fun <VIEW> ViewFactory<VIEW>.horizontal(setup: LinearBuilder<VIEW>.() -> Unit): VIEW {
//    val list = LinearBuilder<VIEW>()
//    list.setup()
//    return horizontal(*list.toTypedArray())
//}
//
//fun <VIEW> ViewFactory<VIEW>.vertical(setup: LinearBuilder<VIEW>.() -> Unit): VIEW {
//    val list = LinearBuilder<VIEW>()
//    list.setup()
//    return vertical(*list.toTypedArray())
//}
//
//fun <VIEW> ViewFactory<VIEW>.linear(defaultToHorizontal: Boolean = false, setup: LinearBuilder<VIEW>.() -> Unit): VIEW {
//    val list = LinearBuilder<VIEW>()
//    list.setup()
//    return linear(defaultToHorizontal = defaultToHorizontal, views = *list.toTypedArray())
//}
//
//class AlignPairBuilder<VIEW>() : MutableList<Pair<AlignPair, VIEW>> by ArrayList<Pair<AlignPair, VIEW>>() {
//
//    var defaultAlign = Align.Fill
//
//    operator fun AlignPair.plus(view: VIEW) {
//        add(this to view)
//    }
//
//    operator fun AlignPair.minus(view: VIEW) {
//        add(this to view)
//    }
//}
//
//fun <VIEW> ViewFactory<VIEW>.align(setup: AlignPairBuilder<VIEW>.() -> Unit): VIEW {
//    val list = AlignPairBuilder<VIEW>()
//    list.setup()
//    return align(*list.toTypedArray())
//}
