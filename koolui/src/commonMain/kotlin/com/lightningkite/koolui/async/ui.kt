package com.lightningkite.koolui.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

expect val Dispatchers.UI: CoroutineDispatcher
