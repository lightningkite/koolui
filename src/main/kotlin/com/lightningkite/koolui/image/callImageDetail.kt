package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*

actual suspend fun HttpClient.callImageDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody,
    headers: Map<String, List<String>>
): HttpResponse<Image> = callByteArrayDetail(
    url, method, body, headers
).copy {
    @Suppress("DEPRECATION")
    Displayable(BitmapDrawable(BitmapFactory.decodeByteArray(it, 0, it.size)))
}
