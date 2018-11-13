package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*


suspend fun HttpClient.callImage(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf()
): Displayable {
    return callImageDetail(url, method, body, headers).result!!
}
