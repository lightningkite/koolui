package com.lightningkite.koolui.image

import com.lightningkite.koolui.resources.Resources
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual suspend fun Resources.getImage(filename: String): Displayable = GlobalScope.async {
    Displayable(
            Resources.getByteArray(filename).inputStream().use { it.readBytes() }
    )
}.await()
