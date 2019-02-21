package com.lightningkite.koolui.image

import com.lightningkite.koolui.resources.Resources
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual suspend fun Resources.getImage(filename: String): Image = GlobalScope.async {
    Image(
            Resources.getByteArray(filename).inputStream().use { it.readBytes() }
    )
}.await()
