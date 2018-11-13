package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*
import com.lightningkite.koolui.resources.Resources

actual suspend fun HttpClient.callImageDetail(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>>
): HttpResponse<Displayable> = callByteArrayDetail(
        url, method, body, headers
).copy {
    Displayable(it)
}
