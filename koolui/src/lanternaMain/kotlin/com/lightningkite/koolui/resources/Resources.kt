package com.lightningkite.koolui.resources

import com.lightningkite.koolui.ApplicationAccess
import com.lightningkite.koolui.image.Image
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual object Resources {

    lateinit var classLoader: ClassLoader

    actual suspend fun getString(
            filename: String
    ): String = GlobalScope.async {
        classLoader.getResourceAsStream(filename).use { it.reader().readText() }
    }.await()

    actual suspend fun getByteArray(
            filename: String
    ): ByteArray = GlobalScope.async {
        classLoader.getResourceAsStream(filename).use { it.readBytes() }
    }.await()

    actual suspend fun getImage(
            filename: String
    ): Image = GlobalScope.async {
        Image.fromByteArray(classLoader.getResourceAsStream(filename).use { it.readBytes() })
    }.await()

}
