package com.lightningkite.koolui.resources

import com.lightningkite.koolui.ApplicationAccess
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

actual object Resources {

    actual suspend fun getString(
            filename: String
    ): String = TODO()

    actual suspend fun getByteArray(
            filename: String
    ): ByteArray = TODO()

}
