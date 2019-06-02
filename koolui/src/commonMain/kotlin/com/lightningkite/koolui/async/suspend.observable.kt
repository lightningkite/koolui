package com.lightningkite.koolui.async

import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun <DETAIL: GENERAL, GENERAL> (suspend ()->DETAIL).observable(temp: GENERAL): ObservableProperty<GENERAL> {
    val obs = StandardObservableProperty(temp)
    GlobalScope.launch(Dispatchers.UI){
        obs.value = this@observable()
    }
    return obs
}