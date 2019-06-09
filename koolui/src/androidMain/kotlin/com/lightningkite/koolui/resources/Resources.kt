package com.lightningkite.koolui.resources

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.image.Image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

actual object Resources {
    actual suspend fun getString(
        filename: String
    ): String = GlobalScope.async {
        ApplicationAccess.access!!.context.assets.open(filename).use { it.reader().readText() }
    }.await()

    actual suspend fun getByteArray(
        filename: String
    ): ByteArray = GlobalScope.async {
        ApplicationAccess.access!!.context.assets.open(filename).use { it.readBytes() }
    }.await()

    actual suspend fun getImage(filename: String): Image = GlobalScope.async {
        Image.fromByteArray(ApplicationAccess.access!!.context.assets.open(filename).use { it.readBytes() })
    }.await()
}
