package com.lightningkite.koolui.resources

import com.lightningkite.koolui.image.Image


expect object Resources {
    suspend fun getString(
        filename: String
    ): String

    suspend fun getByteArray(
        filename: String
    ): ByteArray

    suspend fun getImage(
        filename: String
    ): Image
}
