package com.lightningkite.koolui

import com.lightningkite.kommon.string.*

expect object ExternalAccess {

    fun openUri(uri: Uri)

    fun saveToChosenFile(
            name: String,
            contentType: MediaTypeWithDescription,
            data: ByteArray,
            alwaysOpenDialog: Boolean = false,
            callback: (succeeded: Boolean) -> Unit = {}
    )

    fun loadFromChosenFile(
            contentTypes: List<MediaTypeAcceptWithDescription> = listOf(MediaTypeAcceptWithDescription.any),
            callback: (data: ByteArray?) -> Unit = {}
    )
}