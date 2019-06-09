package com.lightningkite.koolui.async

import com.lightningkite.reacktive.EnablingMutableCollection
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.MutableObservableProperty
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.StandardObservableProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun <A, B> ObservableProperty<A>.suspendingTransform(
        default: B,
        crossinline transformStarted: MutableObservableProperty<B>.(A)->Unit = {},
        scope: CoroutineScope = GlobalScope,
        crossinline transform: suspend (A)->B
): MutableObservableProperty<B> {
    return object : EnablingMutableCollection<(B)->Unit>(), MutableObservableProperty<B> {
        val listener = { it: A ->
            transformStarted(it)
            scope.launch(Dispatchers.UI){
                value = transform(it)
            }
            Unit
        }
        override fun enable() {
            listener(this@suspendingTransform.value)
            this@suspendingTransform.add(listener)
        }

        override fun disable() {
            this@suspendingTransform.remove(listener)
        }

        override var value: B = default
            set(value){
                field = value
                this.invokeAll(value)
            }

    }
}