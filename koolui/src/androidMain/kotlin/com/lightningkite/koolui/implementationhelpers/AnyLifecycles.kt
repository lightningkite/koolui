package com.lightningkite.koolui.implementationhelpers

import com.lightningkite.kommon.collection.WeakHashMap


val AnyLifecycles = WeakHashMap<Any, TreeObservableProperty>()

//var Any.lifecycle
//        get() = AnyLifecycles.getOrPut(this){ TreeObservableProperty() }
//        set(value){
//            AnyLifecycles[this] = value
//        }
//fun Any.lifecycleChildOf(parent:Any){
//    this.lifecycle = parent.lifecycle.child()
//}
