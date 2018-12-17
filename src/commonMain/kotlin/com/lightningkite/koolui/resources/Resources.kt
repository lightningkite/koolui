package com.lightningkite.koolui.resources


expect object Resources {
    suspend fun getString(
        filename: String
    ): String

    suspend fun getByteArray(
        filename: String
    ): ByteArray
}
