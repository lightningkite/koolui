package com.lightningkite.koolui.async

import com.lightningkite.reacktive.Lifecycle
import kotlinx.coroutines.*

val Lifecycle.scope: CoroutineScope
    get() {
        val scope = CoroutineScope(Dispatchers.UI)
        GlobalScope.launch(Dispatchers.UI) {
            var willHoldLambda: (Boolean) -> Unit = {}
            willHoldLambda = {
                if (!it) {
                    scope.cancel()
                    GlobalScope.launch(Dispatchers.UI) {
                        this@scope.remove(willHoldLambda)
                    }
                }
            }
            this@scope.add(willHoldLambda)
        }
        return scope
    }