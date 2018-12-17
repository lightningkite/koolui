package com.lightningkite.koolui.image

import com.lightningkite.kommunicate.*


expect suspend fun HttpClient.callImageDetail(
    url: String,
    method: HttpMethod,
    body: HttpBody = HttpBody.EMPTY,
    headers: Map<String, List<String>> = mapOf()
): HttpResponse<Displayable>
