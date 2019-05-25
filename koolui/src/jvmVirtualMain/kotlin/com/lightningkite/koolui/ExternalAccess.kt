package com.lightningkite.koolui

import com.lightningkite.kommon.string.MediaTypeAcceptWithDescription
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.kommon.string.Uri
import com.lightningkite.reacktive.invokeAll

actual object ExternalAccess {
    val uriOpened = ArrayList<(Uri)->Unit>()
    actual fun openUri(uri: Uri) {
        println("Opened $uri")
        uriOpened.invokeAll(uri)
    }

    val savedToFile = ArrayList<(ByteArray)->Unit>()
    actual fun saveToChosenFile(name: String, contentType: MediaTypeWithDescription, data: ByteArray, alwaysOpenDialog: Boolean, callback: (succeeded: Boolean) -> Unit) {
        println("Saved to file")
        savedToFile.invokeAll(data)
    }

    var dataToLoad: ByteArray? = null
    actual fun loadFromChosenFile(contentTypes: List<MediaTypeAcceptWithDescription>, callback: (data: ByteArray?) -> Unit) {
        println("Load from file")
        dataToLoad?.let(callback) ?: callback(null)
    }

}