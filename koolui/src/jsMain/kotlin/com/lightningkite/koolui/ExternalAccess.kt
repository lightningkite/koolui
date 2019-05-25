package com.lightningkite.koolui

import com.lightningkite.kommon.asByteArray
import com.lightningkite.kommon.asInt8Array
import com.lightningkite.kommon.string.MediaTypeAcceptWithDescription
import com.lightningkite.kommon.string.MediaTypeWithDescription
import com.lightningkite.kommon.string.Uri
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.browser.document
import kotlin.browser.window

actual object ExternalAccess {

    actual fun openUri(uri: Uri) {
        window.open(uri.string)
    }

    actual fun saveToChosenFile(
            name: String,
            contentType: MediaTypeWithDescription,
            data: ByteArray,
            alwaysOpenDialog: Boolean,
            callback: (succeeded: Boolean) -> Unit
    ) {
        val url = URL.createObjectURL(Blob(arrayOf(data.asInt8Array().buffer), BlobPropertyBag("octet/stream")))
        val element = document.createElement("a") as HTMLAnchorElement
        document.body?.appendChild(element)
        element.hidden = true
        element.href = url
        element.download = name + "." + contentType.extensions.first()
        element.click()
        URL.revokeObjectURL(url)
    }

    actual fun loadFromChosenFile(contentTypes: List<MediaTypeAcceptWithDescription>, callback: (data: ByteArray?) -> Unit) {
        val element = document.createElement("input") as HTMLInputElement
        document.body?.appendChild(element)
        element.hidden = true
        element.type = "file"
        element.accept = contentTypes.joinToString { it.mediaTypeAccept.string }
        element.addEventListener("change", { event ->
            val file = element.files?.get(0) ?: run {
                callback(null)
                return@addEventListener
            }
            val reader = FileReader()
            reader.onload = {
                callback(Int8Array(reader.result as ArrayBuffer).asByteArray())
            }
            reader.readAsArrayBuffer(file)
            element.remove()
        })
        element.click()
    }

}