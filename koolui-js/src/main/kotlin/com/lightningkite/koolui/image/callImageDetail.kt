package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*
import kotlinx.io.core.readBytes
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.xhr.BLOB
import org.w3c.xhr.TEXT
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.browser.document
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual suspend fun HttpClient.callImageDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody,
    headers: Map<String, List<String>>
): HttpResponse<Displayable> = suspendCoroutine { callback ->
    val request = XMLHttpRequest()
    request.responseType = XMLHttpRequestResponseType.BLOB
    request.addEventListener("load", callback = { _ ->
        val result = toHttpResponse(request, XMLHttpRequest::responseText) {
            Displayable(URL.createObjectURL(it.response as Blob))
        }
        callback.resume(result)
    })
    request.addEventListener("error", callback = { _ ->
        val result = toHttpResponse(request, XMLHttpRequest::responseText) {
            Displayable(URL.createObjectURL(it.response as Blob))
        }
        callback.resume(result)
    })
    request.open(method.name, url)
    request.send(
        when (body) {
            is HttpBody.BString -> body.value
            is HttpBody.BByteArray -> body.value
            is HttpBody.BInput -> body.value.readBytes()
        }
    )
}
